package qyoga.exercises

import qyoga.api.exercises.ExerciseEditDto


class ExercisesService(
    private val exercisesRepository: ExercisesRepository
) {

    fun getPage(exercisesPage: ExercisesPage): List<ExerciseEditDto> {
        return exercisesRepository.fetch(exercisesPage)
    }

}