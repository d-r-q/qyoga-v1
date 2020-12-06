package qyoga.exercises

import qyoga.api.exercises.ExerciseEditDto
import qyoga.files.FileService
import qyoga.files.QFile


class ExercisesService(
    private val exercisesRepository: ExercisesRepository,
    private val fileService: FileService
) {

    fun createExercise(exercise: ExerciseEditDto): ExerciseEditDto {
        return exercisesRepository.createExercise(exercise)
    }

    fun getPage(exercisesPage: ExercisesPage): List<ExerciseEditDto> {
        return exercisesRepository.fetch(exercisesPage)
    }

    fun fetchFile(exId: Long, fileIndex: Int): QFile? {
        val fileId = exercisesRepository.findFile(exId, fileIndex)
            ?: return null

        return fileService.fetchFile(fileId)
    }

}