package de.fabiexe.networkink

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CombinedServer(val servers: Iterable<Server>) : Server {
    override suspend fun start(dispatcher: CoroutineContext) {
        for (server in servers) {
            server.start(dispatcher)
        }
    }

    override suspend fun acceptConnections(
        dispatcher: CoroutineContext,
        listener: suspend (Connection) -> Unit
    ) {
        val coroutineScope = CoroutineScope(dispatcher)
        for (server in servers) {
            coroutineScope.launch {
                server.acceptConnections(dispatcher, listener)
            }
        }
    }

    override suspend fun iterator(): Iterator<Connection> {
        return servers.flatMap { it.getConnections() }.iterator()
    }

    override suspend fun getConnections(): Set<Connection> {
        return servers.flatMap { it.getConnections() }.toSet()
    }

    override fun close() {
        for (server in servers) {
            server.close()
        }
    }
}