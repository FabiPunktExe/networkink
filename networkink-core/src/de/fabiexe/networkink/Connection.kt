package de.fabiexe.networkink

import de.fabiexe.networkink.handler.NetworkHandler
import de.fabiexe.networkink.handler.NoopHandler
import de.fabiexe.networkink.transformer.Transformer
import io.ktor.utils.io.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class Connection {
    val incomingTransformers = mutableListOf<Transformer<*, *>>()
    val outgoingTransformers = mutableListOf<Transformer<*, *>>()
    var handler: NetworkHandler<*> = NoopHandler
        private set
    val attributes = mutableMapOf<String, Any>()
    private val writeLock = Mutex()

    protected abstract suspend fun readByteArray(): ByteArray
    protected abstract suspend fun writeByteArray(data: ByteArray)
    abstract fun close()

    suspend fun receive() {
        var transformedData: Any = readByteArray()
        for (transformer in incomingTransformers) {
            if (transformer.inputClass.isInstance(transformedData)) {
                transformedData = transformer.transformAny(transformedData)
            }
        }
        handler.handleAny(this, transformedData)
    }

    suspend fun receiveLoop() {
        try {
            while (true) {
                receive()
            }
        } catch (e: Exception) {
            close()
            handler.disconnected(this, e)
        }
    }

    suspend fun send(data: Any) {
        var transformedData = data
        for (transformer in outgoingTransformers) {
            if (transformer.inputClass.isInstance(transformedData)) {
                transformedData = transformer.transformAny(transformedData)
            }
        }
        if (transformedData !is ByteArray) {
            throw IllegalArgumentException("Transformed data must be a ByteArray")
        }
        writeLock.withLock {
            try {
                writeByteArray(transformedData)
            } catch (_: ClosedWriteChannelException) {
                close()
                handler.disconnected(this, null)
            }
        }
    }

    suspend fun applyHandler(handler: NetworkHandler<*>) {
        this.handler = handler
        handler.initialize(this)
    }
}