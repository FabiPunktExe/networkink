package de.fabiexe.networkink

import de.fabiexe.buffer.Buffer
import de.fabiexe.networkink.packet.PacketType

class TestPacket(val data: String) {
    object Type : PacketType<TestPacket>("networkink-test:test", TestPacket::class) {
        override fun encode(buffer: Buffer, packet: TestPacket) {
            buffer.writeString(packet.data)
        }

        override fun decode(buffer: Buffer): TestPacket {
            return TestPacket(buffer.readString())
        }
    }
}
