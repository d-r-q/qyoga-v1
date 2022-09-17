package qyoga.exercises

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
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
    @DbJsonB
    val instructions: List<Step>,
    val duration: Duration,
    @DbJsonB
    val tags: List<TagId>
) {

    fun with(
        name: String = this.name,
        description: String = this.description,
        instructions: List<Step> = this.instructions,
        duration: Duration = this.duration,
        tags: List<TagId> = this.tags,
    ): ExerciseEntity<ID> {
        return ExerciseEntity(id, name, description, instructions, duration, tags)
    }

}

data class Step @JsonCreator constructor(
    @JsonProperty("description") val description: String,
    @JsonProperty("image") val image: Long?
)