package qyoga.exercises

import qyoga.api.exercises.ExerciseEditDto

class ExercisesPage(val sinceName: String?, val amount: Int)

interface ExercisesRepository {

    fun fetch(page: ExercisesPage): List<ExerciseEditDto>

    fun findFile(exId: Long, fileIndex: Int): Long?

    fun createExercise(exercise: ExerciseEditDto): ExerciseEditDto

    fun updateExercise(exercise: ExerciseEditDto)
}
