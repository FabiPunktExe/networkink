package de.fabiexe.networkink

import de.fabiexe.networkink.handler.NetworkHandler
import kotlin.test.assertEquals
import kotlin.test.assertIs

object TestHandler : NetworkHandler<TestPacket>(TestPacket::class) {
    override suspend fun initialize(connection: Connection) {
        connection.send(TestPacket("foo bar"))
        connection.receive()
    }

    override suspend fun handle(connection: Connection, data: TestPacket) {
        assertIs<TestPacket>(data)
        assertEquals("foo bar", data.data)
    }
}