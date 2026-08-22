package de.fabiexe.networkink

data class ProtocolVersionMismatch(val clientVersion: Int, val serverVersion: Int) :
    RuntimeException("Protocol version mismatch: client version $clientVersion, server version $serverVersion")