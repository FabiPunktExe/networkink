package de.fabiexe.networkink

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/** Represents a server that can accept connections from clients. */
interface Server : AutoCloseable {
    /**
     * Starts the server and begins listening for incoming connections.
     * This method should be called before accepting connections.
     */
    suspend fun start(dispatcher: CoroutineContext = Dispatchers.Default)

    /**
     * Accepts incoming connections and invokes the provided listener for each connection.
     * This method should be called after the server has been started.
     *
     * @param listener A suspend function that will be called for each accepted connection.
     */
    suspend fun acceptConnections(
        dispatcher: CoroutineContext = Dispatchers.Default,
        listener: suspend (Connection) -> Unit
    )

    /**
     * Returns an iterator over the connections managed by the server.
     *
     * @return An iterator over the connections.
     */
    suspend operator fun iterator(): Iterator<Connection>

    /**
     * Returns a set of all connections managed by the server.
     *
     * @return A set of all connections.
     */
    suspend fun getConnections(): Set<Connection>
}