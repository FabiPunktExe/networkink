package de.fabiexe.networkink.packet

import de.fabiexe.buffer.Buffer

class C2SEncryptResponsePacket(val key: ByteArray) {
    object Type : PacketType<C2SEncryptResponsePacket>("c2s/encrypt", C2SEncryptResponsePacket::class) {
        override fun estimateSize(packet: C2SEncryptResponsePacket): Int = sum(
            Int.SIZE_BITS,
            Byte.SIZE_BITS * packet.key.size
        ) / 2

        override fun encode(buffer: Buffer, packet: C2SEncryptResponsePacket) {
            buffer.writeInt(packet.key.size)
            buffer.writeBytes(packet.key)
        }

        override fun decode(buffer: Buffer): C2SEncryptResponsePacket {
            val keyLength = buffer.readInt()
            val key = buffer.readBytes(keyLength)
            return C2SEncryptResponsePacket(key)
        }
    }
}
