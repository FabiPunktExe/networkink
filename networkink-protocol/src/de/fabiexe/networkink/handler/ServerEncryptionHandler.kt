package de.fabiexe.networkink.handler

import de.fabiexe.networkink.Connection
import de.fabiexe.networkink.packet.C2SEncryptResponsePacket
import de.fabiexe.networkink.packet.S2CEncryptRequestPacket
import de.fabiexe.networkink.transformer.DecryptionTransformer
import de.fabiexe.networkink.transformer.EncryptionTransformer
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.RSA

class ServerEncryptionHandler : NetworkHandler<C2SEncryptResponsePacket>(C2SEncryptResponsePacket::class) {
    lateinit var rsaKeyPair: RSA.OAEP.KeyPair

    override suspend fun initialize(connection: Connection) {
        rsaKeyPair = rsa.keyPairGenerator().generateKey()
        val publicKeyBytes = rsaKeyPair.publicKey.encodeToByteArray(RSA.PublicKey.Format.DER)
        connection.send(S2CEncryptRequestPacket(publicKeyBytes))
        connection.receive()
    }

    override suspend fun handle(connection: Connection, data: C2SEncryptResponsePacket) {
        val aesKeyBytes = rsaKeyPair.privateKey.decryptor().decrypt(data.key)
        val aesKey = aes.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, aesKeyBytes)
        connection.incomingTransformers.add(0, DecryptionTransformer(aesKey))
        connection.outgoingTransformers += EncryptionTransformer(aesKey)
    }

    private companion object {
        val rsa = CryptographyProvider.Default.get(RSA.OAEP)
        val aes = CryptographyProvider.Default.get(AES.GCM)
    }
}