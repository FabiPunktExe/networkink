package de.fabiexe.networkink.handler

import de.fabiexe.networkink.Connection
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class NetworkHandler<T : Any>(val type: KClass<T>) {
    open suspend fun initialize(connection: Connection) {}

    open suspend fun handle(connection: Connection, data: T) {
        throw UnsupportedOperationException("This network handler does not support handling data")
    }

    suspend fun handleAny(connection: Connection, data: Any) {
        try {
            handle(connection, type.cast(data))
        } catch (_: ClassCastException){
            throw IllegalArgumentException("Unexpected data/packet type: ${data::class}")
        }
    }

    open suspend fun exceptionThrown(connection: Connection, exception: Exception) {}
    open suspend fun closed(connection: Connection) {}
}