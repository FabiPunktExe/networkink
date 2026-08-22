package de.fabiexe.networkink

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

suspend fun connect(
    host: String,
    port: Int,
    dispatcher: CoroutineContext = Dispatchers.Default,
    readFrame: suspend (ByteReadChannel) -> ByteArray = ::defaultReadFrame,
    writeFrame: suspend (ByteWriteChannel, ByteArray) -> Unit = ::defaultWriteFrame
): SocketConnection {
    val socket = aSocket(SelectorManager(dispatcher)).tcp().connect(host, port)
    return SocketConnection(socket, readFrame, writeFrame)
}