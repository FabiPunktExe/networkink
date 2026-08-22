package de.fabiexe.networkink.transformer

import de.fabiexe.buffer.BitBuffer
import de.fabiexe.networkink.packet.PacketType

class PacketDecoder(packetTypes: Iterable<PacketType<*>>) : Transformer<BitBuffer, Any>(BitBuffer::class) {
    private val packetTypes = packetTypes.associateBy { it.id }

    override fun transform(input: BitBuffer): Any {
        val packetId = input.readString()
        val packetType = packetTypes[packetId]
            ?: throw IllegalStateException("No packet type found for id $packetId")

        val packetSize = input.readInt()
        val packetBuffer = input.readBits(packetSize)
        return packetType.decode(packetBuffer)
    }
}