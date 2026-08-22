package de.fabiexe.networkink.handler

import de.fabiexe.networkink.Connection
import de.fabiexe.networkink.packet.C2SEncryptResponsePacket
import de.fabiexe.networkink.packet.S2CEncryptRequestPacket
import de.fabiexe.networkink.transformer.DecryptionTransformer
import de.fabiexe.networkink.transformer.EncryptionTransformer
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.RSA
import dev.whyoleg.cryptography.algorithms.SHA512

object ClientEncryptionHandler : NetworkHandler<S2CEncryptRequestPacket>(S2CEncryptRequestPacket::class) {
    private val rsa = CryptographyProvider.Default.get(RSA.OAEP)
    private val aes = CryptographyProvider.Default.get(AES.GCM)

    override suspend fun initialize(connection: Connection) {
        connection.receive()
    }

    override suspend fun handle(connection: Connection, data: S2CEncryptRequestPacket) {
        val decoder = rsa.publicKeyDecoder(SHA512)
        val rsaPublicKey = decoder.decodeFromByteArray(RSA.PublicKey.Format.DER, data.publicKey)
        val aesKey = aes.keyGenerator().generateKey()
        val aesKeyBytes = aesKey.encodeToByteArray(AES.Key.Format.RAW)
        val encryptedAesKey = rsaPublicKey.encryptor().encrypt(aesKeyBytes)
        connection.send(C2SEncryptResponsePacket(encryptedAesKey))
        connection.incomingTransformers.add(0, DecryptionTransformer(aesKey))
        connection.outgoingTransformers += EncryptionTransformer(aesKey)
    }
}