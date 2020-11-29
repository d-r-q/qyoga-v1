package qyoga.exercises

import qyoga.api.exercises.ExerciseEditDto

class ExercisesPage(val sinceName: String?, val amount: Int)

interface ExercisesRepository {

    fun fetch(page: ExercisesPage): List<ExerciseEditDto>

}
