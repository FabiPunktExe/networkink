package de.fabiexe.networkink

import io.ktor.network.sockets.*
import io.ktor.utils.io.*

class SocketConnection(
    val socket: Socket,
    val readFrame: suspend (Connection, ByteReadChannel) -> ByteArray,
    val writeFrame: suspend (Connection, ByteWriteChannel, ByteArray) -> Unit
) : Connection() {
    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel()

    override suspend fun readByteArray(): ByteArray {
        return readFrame(this, readChannel)
    }

    override suspend fun writeByteArray(data: ByteArray) {
        writeFrame(this, writeChannel, data)
    }

    override fun close() {
        socket.close()
    }
}

private const val DEFAULT_MAX_FRAME_SIZE = 2 * 1024 * 1024

suspend fun defaultReadFrame(
    connection: Connection,
    readChannel: ByteReadChannel,
    maxFrameSize: Int = DEFAULT_MAX_FRAME_SIZE
): ByteArray {
    val size = readChannel.readInt()
    require(size in 0..maxFrameSize) { "Frame size $size is out of range 0..$maxFrameSize" }

    val byteArray = ByteArray(size)
    readChannel.readFully(byteArray)
    return byteArray
}

suspend fun defaultWriteFrame(connection: Connection, writeChannel: ByteWriteChannel, data: ByteArray) {
    writeChannel.writeInt(data.size)
    writeChannel.writeByteArray(data)
    writeChannel.flush()
}