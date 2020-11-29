package qyoga.db

import io.ebean.Database
import qyoga.domain.DomainId


inline fun <T, reified E> DomainId<T, E>.resolve(db: Database) =
    db.find(E::class.java)
        .findOne()
        ?: throw IllegalStateException("Cannot find entity of type ${E::class} by id $this")

inline fun <T, reified E> List<DomainId<T, E>>.resolve(db: Database): List<E> =
    db.find(E::class.java)
        .where()
        .`in`("id", this)
        .findList()
