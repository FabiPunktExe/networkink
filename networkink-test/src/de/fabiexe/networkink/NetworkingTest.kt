package de.fabiexe.networkink

import de.fabiexe.networkink.handler.ClientEncryptionHandler
import de.fabiexe.networkink.handler.ClientHelloHandler
import de.fabiexe.networkink.handler.ServerEncryptionHandler
import de.fabiexe.networkink.handler.ServerHelloHandler
import de.fabiexe.networkink.packet.C2SEncryptResponsePacket
import de.fabiexe.networkink.packet.S2CEncryptRequestPacket
import de.fabiexe.networkink.transformer.BitsToBytesTransformer
import de.fabiexe.networkink.transformer.BytesToBitsTransformer
import de.fabiexe.networkink.transformer.PacketDecoder
import de.fabiexe.networkink.transformer.PacketEncoder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest

abstract class NetworkingTest {
    val packetTypes = listOf(
        C2SEncryptResponsePacket.Type,
        S2CEncryptRequestPacket.Type,
        TestPacket.Type
    )

    abstract fun createServer(port: Int): Server
    abstract suspend fun connect(port: Int): Connection

    open fun testConnection() {
        runTest {
            val port = findFreePort(10000)

            val server = createServer(port)
            server.start()
            val serverJob = launch {
                server.acceptConnections { connection ->
                    connection.applyHandler(ServerHelloHandler(PROTOCOL_VERSION))
                    connection.incomingTransformers += BytesToBitsTransformer
                    connection.incomingTransformers += PacketDecoder(packetTypes)
                    connection.outgoingTransformers += PacketEncoder(packetTypes)
                    connection.outgoingTransformers += BitsToBytesTransformer
                    connection.applyHandler(TestHandler)
                    connection.send(TestPacket("foo bar"))
                    connection.receiveLoop()
                }
            }

            val clientConnection = connect(port)
            clientConnection.applyHandler(ClientHelloHandler(PROTOCOL_VERSION))
            clientConnection.incomingTransformers += BytesToBitsTransformer
            clientConnection.incomingTransformers += PacketDecoder(packetTypes)
            clientConnection.outgoingTransformers += PacketEncoder(packetTypes)
            clientConnection.outgoingTransformers += BitsToBytesTransformer
            clientConnection.applyHandler(TestHandler)
            clientConnection.send(TestPacket("foo bar"))
            clientConnection.close()

            serverJob.cancelAndJoin()
            server.close()
        }
    }

    open fun testEncryptedConnection() {
        runTest {
            val port = findFreePort(10000)

            val server = createServer(port)
            server.start()
            val serverJob = launch {
                try {
                    server.acceptConnections { connection ->
                        connection.applyHandler(ServerHelloHandler(PROTOCOL_VERSION))
                        connection.incomingTransformers += BytesToBitsTransformer
                        connection.incomingTransformers += PacketDecoder(packetTypes)
                        connection.outgoingTransformers += PacketEncoder(packetTypes)
                        connection.outgoingTransformers += BitsToBytesTransformer
                        connection.applyHandler(ServerEncryptionHandler())
                        connection.applyHandler(TestHandler)
                        connection.close()
                    }
                } catch (_: CancellationException) {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val clientConnection = connect(port)
            clientConnection.applyHandler(ClientHelloHandler(PROTOCOL_VERSION))
            clientConnection.incomingTransformers += BytesToBitsTransformer
            clientConnection.incomingTransformers += PacketDecoder(packetTypes)
            clientConnection.outgoingTransformers += PacketEncoder(packetTypes)
            clientConnection.outgoingTransformers += BitsToBytesTransformer
            clientConnection.applyHandler(ClientEncryptionHandler)
            clientConnection.applyHandler(TestHandler)
            clientConnection.close()

            serverJob.cancelAndJoin()
            server.close()
        }
    }
}
