package qyoga.exercises

import io.ebean.Database
import qyoga.*
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
        val tags = tagIds.resolve(db).associateBy { it -> it.id }
        val images = findImages(map { it.id })
        return map { it.toEditDto(tags, images[it.id] ?: emptyList()) }
    }

    override fun findImage(exId: Long, fileIndex: Int): Long? {
        return db.sqlQuery("SELECT image_id FROM exercises_images ei WHERE ei.exercise_id = :id AND index = :idx")
            .setParameter("id", exId)
            .setParameter("idx", fileIndex)
            .findOne()
            ?.getLong("image_id")
    }

    private fun findImages(exIds: Collection<ExerciseId>): Map<ExerciseId, List<Long>> {
        if (exIds.isEmpty()) {
            return emptyMap()
        }

        val query = """SELECT exercise_id, image_id 
            |          FROM exercises_images ei 
            |          WHERE ei.exercise_id IN (:ids) 
            |          ORDER BY index"""
            .trimMargin()
        return db.sqlQuery(query)
            .setParameter("ids", exIds.map { it.value })
            .mapTo { rs, _ -> ExerciseId(rs.getLong("exercise_id")) to rs.getLong("image_id") }
            .findList()
            .groupBy({ it.first }, { it.second })
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
        updateExerciseImages(storedEntity.id, exercise.images)
            .onError { return it }
        return Success(storedEntity.toEditDto(tags.associateBy { it.id }, exercise.images))
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

    private fun updateExerciseImages(id: ExerciseId, exerciseImages: List<Long>): Outcome<Any> {
        try {
            val deleteCurrent = db.sqlUpdate("DELETE FROM exercises_images ei WHERE ei.exercise_id = :id")
            deleteCurrent.setParameter("id", id.value)
            deleteCurrent.executeNow()

            if (exerciseImages.isEmpty()) {
                return Ok
            }

            val insertNew = db.sqlUpdate(
                "INSERT INTO exercises_images (exercise_id, image_id, index) VALUES (:ex_id, :img_id, :idx)"
            )
            for ((idx, imgId) in exerciseImages.withIndex()) {
                with(insertNew) {
                    setParameter("ex_id", id.value)
                    setParameter("img_id", imgId)
                    setParameter("idx", idx)
                    addBatch()
                }
            }

            insertNew.executeBatch()
            return Ok
        } catch (e: Exception) {
            return Failure(e, "Exercise images updating failed")
        }
    }

}

private fun StoredExercise.toEditDto(tagsEntities: Map<TagId, StoredTag>, images: List<Long>) =
    ExerciseEditDto(
        id = this.id.value,
        name = this.name,
        description = this.description,
        instructions = this.instructions,
        duration = this.duration,
        tags = this.tags.map {
            ApiTag(tagsEntities[it]?.name ?: throw IllegalArgumentException("Cannot resolve tag for id $it"))
        },
        images = images
    )

fun ExerciseEditDto.toEntity(tags: List<TagId>): Exercise {
    return Exercise(
        id?.let { ExerciseId(it) },
        name,
        description,
        instructions,
        duration,
        tags
    )
}

class ExerciseIdConverter : DomainIdConverter<ExerciseId, Long, StoredExercise>(::ExerciseId)

class TagIdConverter : DomainIdConverter<TagId, Long, StoredTag>(::TagId)
