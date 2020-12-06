package qyoga.exercises

import qyoga.domain.DomainId
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Embeddable
data class TagId(override val value: Long) : DomainId<Long, StoredTag> {

    constructor(value: Number) : this(value.toLong())

}

typealias Tag = TagEntity<TagId?>
typealias NewTag = TagEntity<TagId?>
typealias StoredTag = TagEntity<TagId>

@Entity
@Table(name = "tags")
class TagEntity<ID : TagId?>(
    @Id
    val id: ID,
    val name: String
)

