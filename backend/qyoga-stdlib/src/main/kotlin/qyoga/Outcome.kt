package qyoga


/**
 * Aborts current thread or coroutine.
 *
 * Intended to use to halt execution being detected programming bug
 */
fun panic(msg: String): Nothing = throw AssertionError(msg)

/**
 * Result type for functions expected to fail.
 *
 * Has two self-explaining variants:
 * - GenericSuccess
 * - GenericError
 *
 * May be used when there are actually no variance in function's results or failures,
 * or when handler do not interested in actual outcome (e.g. error logging interceptor)
 */
@Suppress("UNCHECKED_CAST")
sealed class Outcome<out R : Any?> {

    inline fun onSuccess(body: (R) -> Unit) {
        if (this is GenericSuccess) {
            body(this.result)
        }
    }

    inline fun onError(body: (GenericFailure<R>) -> Nothing): R {
        return when (this) {
            is GenericSuccess -> this.result
            is GenericFailure<*> -> body(this as GenericFailure<R>)
        }
    }

    fun onErrorThrow(): R {
        return when (this) {
            is GenericSuccess -> result
            is GenericFailure<*> -> throw cause
        }
    }

    inline fun <R2, O : Outcome<R2>> flatMap(body: (R) -> O): Outcome<R2> {
        return when (this) {
            is GenericSuccess -> body(result)
            is GenericFailure<*> -> this
        }
    }

}

/**
 * Success outcome base
 */
sealed class GenericSuccess<T>(val result: T) : Outcome<T>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenericSuccess<*>

        if (result != other.result) return false

        return true
    }

    override fun hashCode(): Int {
        return result?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Success(result=$result)"
    }

}

/**
 * Failure outcome base
 */
sealed class GenericFailure<out R>(val message: String, open val cause: Exception) : Outcome<Nothing>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenericFailure<*>

        if (message != other.message) return false
        if (cause != other.cause) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + cause.hashCode()
        return result
    }

    override fun toString(): String {
        return "Error(message=$message, cause=$cause)"
    }

}

/**
 * Success outcome for Unit-functions
 */
object Ok : GenericSuccess<Any>(Unit)

/**
 * Base type for application-specific success outcomes. May be used itself, if function has single kind of success outcome
 */
open class Success<T>(value: T) : GenericSuccess<T>(value)

fun <T> Ok(value: T) = Success(value)

/**
 * Base type for application-specific failure outcomes. May be used itself, if function has single kind of failure outcome
 */
open class Failure<T : Exception>(override val cause: T, message: String = cause.message ?: cause.toString()) :
    GenericFailure<Nothing>(message, cause)

@Suppress("NOTHING_TO_INLINE")
/**
 * Shorthand factory for Failure, using kotlin.Exception with the same message as actual cause
 */
inline fun <T> Failure(msg: String): Outcome<T> = Failure(Exception(msg))

inline fun <R> ergo(body: () -> Outcome<R>): Outcome<R> =
    try {
        body()
    } catch (e: Exception) {
        Failure(cause = e)
    }