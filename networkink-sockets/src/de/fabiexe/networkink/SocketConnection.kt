package de.fabiexe.networkink

import io.ktor.network.sockets.*
import io.ktor.utils.io.*

class SocketConnection(val socket: Socket) : Connection() {
    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel()

    override suspend fun readByteArray(): ByteArray {
        val size = readChannel.readInt()
        val byteArray = ByteArray(size)
        readChannel.readFully(byteArray)
        return byteArray
    }

    override suspend fun writeByteArray(data: ByteArray) {
        writeChannel.writeInt(data.size)
        writeChannel.writeByteArray(data)
        writeChannel.flush()
    }

    override fun close() {
        socket.close()
    }
}