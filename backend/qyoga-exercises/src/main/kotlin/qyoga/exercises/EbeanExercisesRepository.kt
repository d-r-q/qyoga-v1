package qyoga.exercises

import io.ebean.Database
import qyoga.*
import qyoga.api.exercises.ExerciseEditDto
import qyoga.api.exercises.StepDto
import qyoga.db.DomainIdConverter
import qyoga.db.resolve
import qyoga.api.exercises.Tag as ApiTag


@Suppress("UNCHECKED_CAST")
internal class EbeanExercisesRepository(
    private val db: Database
) : ExercisesRepository {

    override fun fetch(page: ExercisesPage): List<ExerciseEditDto> {
        val exercises = db.find(ExerciseEntity::class.java)
            .where()
            .ge("name", page.sinceName ?: "a")
            .orderBy("name")
            .setMaxRows(page.amount)
            .findList() as List<StoredExercise>
        return exercises.toEditDtos()
    }

    override fun fetch(exerciseIds: List<ExerciseId>): List<ExerciseEditDto> {
        val exercises = db.find(ExerciseEntity::class.java)
            .where()
            .`in`("id", exerciseIds.map { it.value })
            .findList() as List<StoredExercise>
        return exercises.toEditDtos()
    }

    private fun List<ExerciseEntity<ExerciseId>>.toEditDtos(): List<ExerciseEditDto> {
        val tagIds = flatMap { it.tags }
        val tags = tagIds.resolve(db).associateBy { it: TagEntity<TagId> -> it.id }
        return map { it.toEditDto(tags) }
    }

    override fun persistExercise(exercise: ExerciseEditDto): Outcome<ExerciseEditDto> = ergo {
        val tags = mergeTags(exercise.tags)
        val storedEntity = with(exercise.toEntity(tags.map { it.id })) {
            if (this.id == null) {
                db.save(this)
            } else {
                db.update(this)
            }
            this as StoredExercise
        }
        return Success(storedEntity.toEditDto(tags.associateBy { it.id }))
    }

    private fun mergeTags(tags: List<ApiTag>): List<StoredTag> {
        val existingTags = db.find(TagEntity::class.java)
            .where()
            .`in`("name", tags.map { it.tag })
            .findList() as List<StoredTag>
        val existingTagsSet = existingTags.map { it.name }.toSet()

        val missingTags = tags
            .filter { it.tag !in existingTagsSet }
            .map { NewTag(null, it.tag) }
        db.saveAll(missingTags)
        return existingTags + (missingTags as List<StoredTag>)
    }

}

private fun StoredExercise.toEditDto(tagsEntities: Map<TagId, StoredTag>) =
    ExerciseEditDto(
        id = this.id.value,
        name = this.name,
        description = this.description,
        instructions = this.instructions.map { StepDto(it.description, it.image) },
        duration = this.duration,
        tags = this.tags.map {
            ApiTag(tagsEntities[it]?.name ?: throw IllegalArgumentException("Cannot resolve tag for id $it"))
        },
    )

fun ExerciseEditDto.toEntity(tags: List<TagId>): Exercise {
    return Exercise(
        id?.let { ExerciseId(it) },
        name,
        description,
        instructions.map { Step(it.description, it.imageId) },
        duration,
        tags
    )
}

class ExerciseIdConverter : DomainIdConverter<ExerciseId, Long, StoredExercise>(::ExerciseId)

class TagIdConverter : DomainIdConverter<TagId, Long, StoredTag>(::TagId)
