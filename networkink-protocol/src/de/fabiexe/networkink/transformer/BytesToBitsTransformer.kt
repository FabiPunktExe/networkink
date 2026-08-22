package de.fabiexe.networkink.transformer

import de.fabiexe.buffer.BitBuffer
import de.fabiexe.buffer.ByteBuffer

object BytesToBitsTransformer : Transformer<ByteArray, BitBuffer>(ByteArray::class) {
    override fun transform(input: ByteArray): BitBuffer {
        val sizeBuffer = ByteBuffer(input.copyOfRange(0, 4))
        val bitBuffer = BitBuffer(input.copyOfRange(4, input.size))
        bitBuffer.size = sizeBuffer.readInt()
        return bitBuffer
    }
}