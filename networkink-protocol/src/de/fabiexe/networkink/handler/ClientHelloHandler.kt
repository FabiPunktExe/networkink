package de.fabiexe.networkink.handler

import de.fabiexe.networkink.Connection
import de.fabiexe.networkink.ProtocolInfo
import de.fabiexe.networkink.ProtocolVersionMismatch
import kotlinx.serialization.json.Json

data class ClientHelloHandler(val protocolVersion: Int) : NetworkHandler<ByteArray>(ByteArray::class) {
    override suspend fun initialize(connection: Connection) {
        connection.receive()
    }

    override suspend fun handle(connection: Connection, data: ByteArray) {
        val protocolInfo = Json.decodeFromString<ProtocolInfo>(data.decodeToString())
        if (protocolInfo.protocolVersion != protocolVersion) {
            throw ProtocolVersionMismatch(protocolInfo.protocolVersion, protocolVersion)
        }
    }
}