package de.fabiexe.networkink.transformer

import de.fabiexe.buffer.BitBuffer
import de.fabiexe.buffer.ByteBuffer

object BitsToBytesTransformer : Transformer<BitBuffer, ByteArray>(BitBuffer::class) {
    override fun transform(input: BitBuffer): ByteArray {
        val sizeBuffer = ByteBuffer(4)
        sizeBuffer.writeInt(input.size)
        return sizeBuffer.bytes + input.bytes
    }
}