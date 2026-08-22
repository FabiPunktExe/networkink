package de.fabiexe.networkink

import kotlin.test.Test

class SocketsNetworkingTest : NetworkingTest() {
    override fun createServer(port: Int): Server {
        return SocketServer(port)
    }

    override suspend fun connect(port: Int): Connection {
        return connect("localhost", port)
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