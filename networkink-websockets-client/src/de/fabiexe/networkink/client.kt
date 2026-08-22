package de.fabiexe.networkink

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

suspend fun connect(url: String, dispatcher: CoroutineContext = Dispatchers.Default): WebSocketConnection {
    val httpClient = HttpClient { install(WebSockets) }
    val deferredConnection = CompletableDeferred<WebSocketConnection>()
    CoroutineScope(dispatcher).launch {
        httpClient.webSocket(url) {
            val connection = WebSocketConnection(this)
            deferredConnection.complete(connection)
            connection.closeSignal.join()
        }
    }
    return deferredConnection.await()
}