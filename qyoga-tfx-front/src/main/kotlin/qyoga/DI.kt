package qyoga

import tornadofx.DIContainer
import kotlin.reflect.KClass


class DI(private val components: Set<Any>) : DIContainer {
    override fun <T : Any> getInstance(type: KClass<T>): T {
        return components.filterIsInstance(type.java).firstOrNull()
            ?: throw IllegalArgumentException("Component of type $type isn't registered")

    }
}