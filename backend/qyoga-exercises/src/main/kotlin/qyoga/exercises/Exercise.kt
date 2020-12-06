package qyoga.exercises

import io.ebean.annotation.DbJsonB
import qyoga.domain.DomainId
import java.time.Duration
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


data class ExerciseId(override val value: Long) : DomainId<Long, StoredExercise>

typealias Exercise = ExerciseEntity<ExerciseId?>
typealias NewExercise = ExerciseEntity<ExerciseId?>
typealias StoredExercise = ExerciseEntity<ExerciseId>

@Entity
@Table(name = "exercises")
class ExerciseEntity<ID : ExerciseId?>(
    @Id
    val id: ID,
    val name: String,
    val description: String,
    val instructions: String,
    val duration: Duration,
    @DbJsonB
    val tags: List<TagId>
)

