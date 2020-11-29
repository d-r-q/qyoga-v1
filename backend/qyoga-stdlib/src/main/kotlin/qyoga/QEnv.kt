@file:Suppress("UNCHECKED_CAST")

package qyoga

import io.ktor.config.*
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType


class QEnv(private val env: ApplicationConfig) {

    inline operator fun <reified T> get(name: String): T? {
        return getImpl(name, T::class.starProjectedType)
    }

    fun <T> getImpl(name: String, type: KType): T? {
        return when (type) {
            String::class.starProjectedType -> env.propertyOrNull(name)?.getString()
                ?: throw IllegalArgumentException("Cannot resolve property $name of type $type")
            Int::class.starProjectedType -> env.propertyOrNull(name)?.getString()?.toIntOrNull()
            else -> null
        } as T?
    }

}