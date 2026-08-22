package de.fabiexe.networkink

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

class WebSocketServer(private val ktorEngine: ApplicationEngineFactory<*, *>, val port: Int) : DefaultServer() {
    private var listener: CompletableDeferred<suspend (Connection) -> Unit>? = null
    private var server: EmbeddedServer<*, *>? = null

    override suspend fun start(dispatcher: CoroutineContext) {
        listener = CompletableDeferred()

        val closeScope = CoroutineScope(dispatcher)
        server = embeddedServer(factory = ktorEngine, port = port) {
            install(WebSockets) {
                pingPeriod = 15.seconds
                timeout = 10.seconds
            }
            routing {
                webSocket("/") {
                    val connection = WebSocketConnection(this)

                    // Add connection
                    connectionsLock.withLock {
                        connections += connection
                    }

                    // Remove connection on close
                    closeScope.launch {
                        closeReason.join()
                        connectionsLock.withLock {
                            connections -= connection
                        }
                    }

                    listener!!.await()(connection)
                    connection.closeSignal.join()
                }
            }
        }
        server!!.startSuspend()
    }

    override suspend fun acceptConnections(
        dispatcher: CoroutineContext,
        listener: suspend (Connection) -> Unit
    ) {
        if (server == null) {
            throw IllegalStateException("Server not started. Call start() before acceptConnections().")
        }
        this.listener!!.complete(listener)
    }

    override fun close() {
        server?.stop()
        listener?.cancel()
        listener = null
    }
}