package de.fabiexe.networkink.transformer

import dev.whyoleg.cryptography.operations.Encryptor

class EncryptionTransformer(
    val encryptorProvider: () -> Encryptor
) : Transformer<ByteArray, ByteArray>(ByteArray::class) {
    override fun transform(input: ByteArray): ByteArray {
        return encryptorProvider().encryptBlocking(input)
    }
}