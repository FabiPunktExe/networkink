package de.fabiexe.networkink

import io.ktor.network.sockets.*
import io.ktor.utils.io.*

class SocketConnection(
    val socket: Socket,
    val readFrame: suspend (ByteReadChannel) -> ByteArray,
    val writeFrame: suspend (ByteWriteChannel, ByteArray) -> Unit
) : Connection() {
    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel()

    override suspend fun readByteArray(): ByteArray {
        return readFrame(readChannel)
    }

    override suspend fun writeByteArray(data: ByteArray) {
        writeFrame(writeChannel, data)
    }

    override fun close() {
        socket.close()
    }
}

suspend fun defaultReadFrame(readChannel: ByteReadChannel): ByteArray {
    val size = readChannel.readInt()
    val byteArray = ByteArray(size)
    readChannel.readFully(byteArray)
    return byteArray
}

suspend fun defaultWriteFrame(writeChannel: ByteWriteChannel, data: ByteArray) {
    writeChannel.writeInt(data.size)
    writeChannel.writeByteArray(data)
    writeChannel.flush()
}