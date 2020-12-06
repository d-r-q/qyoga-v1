package qyoga.exercises

import qyoga.api.exercises.ExerciseEditDto


class ExercisesService(
    private val exercisesRepository: ExercisesRepository
) {

    fun createExercise(exercise: ExerciseEditDto): ExerciseEditDto {
        // todo: add transaction
        return exercisesRepository.createExercise(exercise)
    }

    fun getPage(exercisesPage: ExercisesPage): List<ExerciseEditDto> {
        return exercisesRepository.fetch(exercisesPage)
    }

}