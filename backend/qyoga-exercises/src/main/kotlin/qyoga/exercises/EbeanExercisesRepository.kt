package qyoga.exercises

import io.ebean.Database
import qyoga.api.exercises.ExerciseEditDto
import qyoga.db.DomainIdConverter
import qyoga.db.resolve


class EbeanExercisesRepository(
    private val db: Database
) : ExercisesRepository {

    override fun fetch(page: ExercisesPage): List<ExerciseEditDto> {
        @Suppress("UNCHECKED_CAST")
        val exercises = db.find(ExerciseEntity::class.java)
            .where()
            .ge("name", page.sinceName ?: "a")
            .setMaxRows(page.amount)
            .findList() as List<StoredExercise>
        val tagIds = exercises.flatMap { it.tags }
        val resolve: List<Tag> = tagIds.resolve(db)
        val tags = resolve.associateBy { it.id }
        return exercises.map { it.toEditDto(tags) }
    }

}

private fun StoredExercise.toEditDto(tags: Map<TagId, StoredTag>) =
    ExerciseEditDto(
        this.id.value,
        this.name,
        this.annotation,
        this.description,
        this.duration,
        this.tags.map {
            qyoga.api.exercises.Tag(
                tags[it]?.name ?: throw IllegalArgumentException("Cannot resolve tag for id $it")
            )
        },
        emptyList()
    )

class ExerciseIdConverter : DomainIdConverter<ExerciseId, Long, StoredExercise>(::ExerciseId)

class TagIdConverter : DomainIdConverter<TagId, Long, StoredTag>(::TagId)
