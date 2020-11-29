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

typealias Tag = TagEntity<TagId>
typealias NewTag = TagEntity<TagId>
typealias StoredTag = TagEntity<TagId>

@Entity
@Table(name = "tags")
class TagEntity<ID : TagId?>(
    @Id
    val id: ID,
    val name: String
)

fun searchLowerBound(list: List<Int>, value: Int): Int {
    tailrec fun impl(left: Int, right: Int): Int {
        val mid = ((left.toLong() + right.toLong()) / 2).toInt()
        return when {
            left > right -> list.size
            left == right && value <= list[left] -> left
            list[mid] < value -> impl(mid + 1, right)
            else -> impl(left, mid)
        }
    }
    return impl(0, list.size - 1)
}


fun main() {

    searchLowerBound(listOf(6, 7, 7, 7, 9, 9), 8)
}