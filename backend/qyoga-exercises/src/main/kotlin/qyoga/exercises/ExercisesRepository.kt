package qyoga.exercises

import qyoga.Outcome
import qyoga.api.exercises.ExerciseEditDto

class ExercisesPage(val sinceName: String?, val amount: Int)

interface ExercisesRepository {

    fun fetch(page: ExercisesPage): List<ExerciseEditDto>

    fun fetch(exerciseIds: List<ExerciseId>): List<ExerciseEditDto>

    fun persistExercise(exercise: ExerciseEditDto): Outcome<ExerciseEditDto>

}
