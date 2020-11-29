package qyoga.exercises

import qyoga.db.DbModule


class ExercisesModule(
    dbModule: DbModule
) {

    private val exercisesRepository = EbeanExercisesRepository(dbModule.ebeanDb)

    val exercisesService = ExercisesService(exercisesRepository)

}