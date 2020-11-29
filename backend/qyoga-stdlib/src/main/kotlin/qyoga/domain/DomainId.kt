package qyoga.domain


interface DomainId<out T, out E> {
    val value: T
}