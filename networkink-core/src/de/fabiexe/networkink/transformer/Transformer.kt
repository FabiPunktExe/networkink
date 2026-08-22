package de.fabiexe.networkink.transformer

import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class Transformer<Input : Any, Output : Any>(val inputClass: KClass<Input>) {
    abstract fun transform(input: Input): Output

    fun transformAny(input: Any): Any = transform(inputClass.cast(input))
}