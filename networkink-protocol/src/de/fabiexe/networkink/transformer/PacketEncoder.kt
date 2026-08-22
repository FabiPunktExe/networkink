package de.fabiexe.networkink.transformer

import de.fabiexe.buffer.BitBuffer
import de.fabiexe.networkink.packet.PacketType

class PacketEncoder(packetTypes: Iterable<PacketType<*>>) : Transformer<Any, BitBuffer>(Any::class) {
    private val packetTypes = packetTypes.associateBy { it.packetClass }

    override fun transform(input: Any): BitBuffer {
        val packetType = packetTypes[input::class]
            ?: throw IllegalStateException("No packet type found for class ${input::class}")

        val packetBuffer = BitBuffer(packetType.estimateSizeAny(input))
        packetType.encodeAny(packetBuffer, input)
        packetBuffer.flip()

        val buffer = BitBuffer()
        buffer.writeString(packetType.id)
        buffer.writeInt(packetBuffer.size)
        buffer.writeBits(packetBuffer)
        buffer.flip()
        return buffer
    }
}