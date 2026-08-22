package de.fabiexe.networkink

import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred

class WebSocketConnection(val session: WebSocketSession) : Connection() {
    val closeSignal = CompletableDeferred<Unit>()

    override suspend fun readByteArray(): ByteArray {
        for (frame in session.incoming) {
            if (frame is Frame.Binary) {
                return frame.readBytes()
            }
        }
        throw IllegalStateException("No binary frame received")
    }

    override suspend fun writeByteArray(data: ByteArray) {
        session.outgoing.send(Frame.Binary(true, data))
    }

    override fun close() {
        closeSignal.complete(Unit)
    }
}