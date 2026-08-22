@file:JvmName("BufferUtil")

package de.fabiexe.networkink.util

import de.fabiexe.buffer.Buffer
import kotlin.jvm.JvmName
import kotlin.uuid.Uuid

fun Buffer.writeUuid(uuid: Uuid) = writeBytes(uuid.toByteArray())
fun Buffer.readUuid(): Uuid = Uuid.fromByteArray(readBytes(Uuid.SIZE_BYTES))