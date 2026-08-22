package de.fabiexe.networkink.handler

import de.fabiexe.networkink.Connection
import de.fabiexe.networkink.ProtocolInfo
import kotlinx.serialization.json.Json

data class ServerHelloHandler(val protocolVersion: Int) : NetworkHandler<Nothing>(Nothing::class) {
    override suspend fun initialize(connection: Connection) {
        val protocolInfo = ProtocolInfo(protocolVersion)
        connection.send(Json.encodeToString(protocolInfo).encodeToByteArray())
    }
}