package qyoga.exercises

import qyoga.db.DbModule


class ExercisesCrudModule(dbModule: DbModule) {

    private val exercisesRepository: ExercisesRepository = EbeanExercisesRepository(dbModule.ebeanDb)

    val exercisesService = ExercisesService(dbModule, exercisesRepository)

}