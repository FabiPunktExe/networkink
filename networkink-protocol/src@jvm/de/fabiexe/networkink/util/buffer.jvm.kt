@file:JvmName("BufferUtilJvm")

package de.fabiexe.networkink.util

import de.fabiexe.buffer.Buffer
import java.util.*
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun Buffer.writeJavaUuid(uuid: UUID) = writeUuid(uuid.toKotlinUuid())
fun Buffer.readJavaUuid(): UUID = readUuid().toJavaUuid()