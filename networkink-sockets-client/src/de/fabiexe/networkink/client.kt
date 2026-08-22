package de.fabiexe.networkink

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

suspend fun connect(host: String, port: Int, dispatcher: CoroutineContext = Dispatchers.Default): SocketConnection {
    return SocketConnection(aSocket(SelectorManager(dispatcher)).tcp().connect(host, port))
}