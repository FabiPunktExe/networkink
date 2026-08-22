package de.fabiexe.networkink

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers

@Throws(IllegalStateException::class)
suspend fun findFreePort(minPort: Int): Int {
    SelectorManager(Dispatchers.Default).use { selectorManager ->
        for (port in minPort..65535) {
            try {
                val socket = aSocket(selectorManager).tcp().bind(port = port)
                socket.close()
                socket.awaitClosed()
                return port
            } catch (_: Exception) {}
        }
        throw IllegalStateException("No free port found in the range $minPort-65535")
    }
}

internal const val PROTOCOL_VERSION = 1