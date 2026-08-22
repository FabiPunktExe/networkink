package de.fabiexe.networkink

import io.ktor.server.cio.*
import kotlin.test.Test

class WebSocketsNetworkingTest : NetworkingTest() {
    override fun createServer(port: Int): Server {
        return WebSocketServer(CIO, port)
    }

    override suspend fun connect(port: Int): Connection {
        return connect("ws://localhost:$port")
    }

    @Test
    override fun testConnection() {
        return super.testConnection()
    }

    @Test
    override fun testEncryptedConnection() {
        super.testEncryptedConnection()
    }
}