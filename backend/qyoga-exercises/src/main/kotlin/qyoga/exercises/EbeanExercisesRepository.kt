package qyoga.exercises

import io.ebean.Database
import qyoga.api.exercises.ExerciseEditDto
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
            .setMaxRows(page.amount)
            .findList() as List<StoredExercise>
        val tagIds = exercises.flatMap { it.tags }
        val resolve = tagIds.resolve(db)
        val tags = resolve.associateBy { it.id }
        val images = findImagesCount(exercises.map { it.id })
        return exercises.map { it.toEditDto(tags, images) }
    }

    override fun findFile(exId: Long, fileIndex: Int): Long? {
        return db.sqlQuery("SELECT image_id FROM exercises_images ei WHERE ei.exercise_id = :id AND index = :idx")
            .setParameter("id", exId)
            .setParameter("idx", fileIndex)
            .findOne()
            ?.getLong("image_id")
    }

    private fun findImagesCount(exIds: Collection<ExerciseId>): Map<ExerciseId, Int> {
        val query = """SELECT exercise_id, count(*) cnt
            |          FROM exercises_images ei 
            |          WHERE ei.exercise_id IN (:ids) 
            |          GROUP BY exercise_id"""
            .trimMargin()
        return db.sqlQuery(query)
            .setParameter("ids", exIds.map { it.value })
            .mapTo { rs, idx -> ExerciseId(rs.getLong("exercise_id")) to rs.getInt("cnt") }
            .findList()
            .toMap()
    }

    override fun createExercise(exercise: ExerciseEditDto): ExerciseEditDto {
        val tags = mergeTags(exercise.tags)
        val newExercise = NewExercise(exercise, tags.map { it.id })
        db.save(newExercise)
        return (newExercise as StoredExercise).toEditDto(tags.associateBy { it.id }, emptyMap())
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

private fun StoredExercise.toEditDto(tagsEntities: Map<TagId, StoredTag>, images: Map<ExerciseId, Int>) =
    ExerciseEditDto(
        id = this.id.value,
        name = this.name,
        description = this.description,
        instructions = this.instructions,
        duration = this.duration,
        tags = this.tags.map {
            ApiTag(tagsEntities[it]?.name ?: throw IllegalArgumentException("Cannot resolve tag for id $it"))
        },
        images = images[id]
            ?.let { to -> (1..to).map { "exercises/${this.id.value}/images/${it}" } }
            ?: emptyList()
    )

fun NewExercise(exercise: ExerciseEditDto, tags: List<TagId>): NewExercise {
    return NewExercise(null, exercise.name, exercise.description, exercise.instructions, exercise.duration, tags)
}

class ExerciseIdConverter : DomainIdConverter<ExerciseId, Long, StoredExercise>(::ExerciseId)

class TagIdConverter : DomainIdConverter<TagId, Long, StoredTag>(::TagId)
