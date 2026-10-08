package de.fabiexe.networkink.transformer

import dev.whyoleg.cryptography.operations.Decryptor

class DecryptionTransformer(
    val decryptorProvider: () -> Decryptor
) : Transformer<ByteArray, ByteArray>(ByteArray::class) {
    override fun transform(input: ByteArray): ByteArray {
        return decryptorProvider().decryptBlocking(input)
    }
}