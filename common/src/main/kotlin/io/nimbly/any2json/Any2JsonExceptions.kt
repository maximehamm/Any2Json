package io.nimbly.any2json

import java.lang.RuntimeException

class Any2PojoException(message: String?) : RuntimeException(message)

class Any2JsonConversionException(message: String) : RuntimeException(message)