package qyoga.exercises

import qyoga.Outcome
import qyoga.api.exercises.ExerciseEditDto
import qyoga.db.DbModule


class ExercisesService(
    private val dbModule: DbModule,
    private val exercisesRepository: ExercisesRepository
) {

    fun persistExercise(exercise: ExerciseEditDto): Outcome<ExerciseEditDto> {
        return dbModule.transaction {
            exercisesRepository.persistExercise(exercise)
        }
    }

    fun getPage(exercisesPage: ExercisesPage): List<ExerciseEditDto> {
        return exercisesRepository.fetch(exercisesPage)
    }

    fun fetch(exerciseIds: List<ExerciseId>): List<ExerciseEditDto> {
        return exercisesRepository.fetch(exerciseIds)
    }

}