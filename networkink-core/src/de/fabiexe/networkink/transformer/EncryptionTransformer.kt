package de.fabiexe.networkink.transformer

import dev.whyoleg.cryptography.algorithms.AES

class EncryptionTransformer(val key: AES.GCM.Key) : Transformer<ByteArray, ByteArray>(ByteArray::class) {
    override fun transform(input: ByteArray): ByteArray {
        return key.cipher().encryptBlocking(input)
    }
}