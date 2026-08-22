package de.fabiexe.networkink

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

class SocketServer(val port: Int) : DefaultServer() {
    private var socket: ServerSocket? = null

    override suspend fun start(dispatcher: CoroutineContext) {
        val selectorManager = SelectorManager(dispatcher)
        socket = aSocket(selectorManager).tcp().bind(port = port)
    }

    override suspend fun acceptConnections(
        dispatcher: CoroutineContext,
        listener: suspend (Connection) -> Unit
    ) {
        if (socket == null) {
            throw IllegalStateException("Server not started. Call start() before acceptConnections().")
        }

        val closeScope = CoroutineScope(dispatcher)
        while (true) {
            val clientSocket = socket!!.accept()
            val connection = SocketConnection(clientSocket)

            // Add connection
            connectionsLock.withLock {
                connections += connection
            }

            // Remove connection on close
            closeScope.launch {
                clientSocket.awaitClosed()
                connectionsLock.withLock {
                    connections -= connection
                }
            }

            listener(connection)
        }
    }

    override fun close() {
        socket?.close()
    }
}