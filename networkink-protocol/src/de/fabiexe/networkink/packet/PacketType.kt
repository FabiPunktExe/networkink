package de.fabiexe.networkink.packet

import de.fabiexe.buffer.Buffer
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class PacketType<T : Any>(val id: String, val packetClass: KClass<T>) {
    open fun estimateSize(packet: T): Int = 0
    abstract fun encode(buffer: Buffer, packet: T)
    abstract fun decode(buffer: Buffer): T

    fun estimateSizeAny(packet: Any) = estimateSize(packetClass.cast(packet))
    fun encodeAny(buffer: Buffer, packet: Any) = encode(buffer, packetClass.cast(packet))

    protected fun sum(vararg sizes: Int): Int {
        return sizes.sum()
    }
}