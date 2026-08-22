package de.fabiexe.networkink

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class DefaultServer : Server {
    protected val connectionsLock = Mutex()
    protected val connections = mutableSetOf<Connection>()

    override suspend operator fun iterator(): Iterator<Connection> = connectionsLock.withLock {
        connections.toSet().iterator()
    }

    override suspend fun getConnections(): Set<Connection> = connectionsLock.withLock {
        connections.toSet()
    }
}