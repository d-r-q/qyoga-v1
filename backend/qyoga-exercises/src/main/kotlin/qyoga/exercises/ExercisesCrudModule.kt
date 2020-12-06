package qyoga.exercises

import qyoga.db.DbModule
import qyoga.files.FilesModule


class ExercisesCrudModule(dbModule: DbModule, filesModule: FilesModule) {

    private val exercisesRepository = EbeanExercisesRepository(dbModule.ebeanDb)

    val exercisesService = ExercisesService(exercisesRepository, filesModule.filesService)

}