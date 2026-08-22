package de.fabiexe.networkink.packet

import de.fabiexe.buffer.Buffer

class S2CEncryptRequestPacket(val publicKey: ByteArray) {
    object Type : PacketType<S2CEncryptRequestPacket>("s2c/encrypt", S2CEncryptRequestPacket::class) {
        override fun estimateSize(packet: S2CEncryptRequestPacket): Int = sum(
            Int.SIZE_BITS,
            Byte.SIZE_BITS * packet.publicKey.size
        ) / 2

        override fun encode(buffer: Buffer, packet: S2CEncryptRequestPacket) {
            buffer.writeInt(packet.publicKey.size)
            buffer.writeBytes(packet.publicKey)
        }

        override fun decode(buffer: Buffer): S2CEncryptRequestPacket {
            val publicKeyLength = buffer.readInt()
            val publicKey = buffer.readBytes(publicKeyLength)
            return S2CEncryptRequestPacket(publicKey)
        }
    }
}
