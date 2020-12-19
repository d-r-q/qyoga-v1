package qyoga.exercises

import qyoga.Outcome
import qyoga.api.exercises.ExerciseEditDto

class ExercisesPage(val sinceName: String?, val amount: Int)

interface ExercisesRepository {

    fun fetch(page: ExercisesPage): List<ExerciseEditDto>

    fun findFile(exId: Long, fileIndex: Int): Long?

    fun persistExercise(exercise: ExerciseEditDto): Outcome<ExerciseEditDto, Exception>

}
