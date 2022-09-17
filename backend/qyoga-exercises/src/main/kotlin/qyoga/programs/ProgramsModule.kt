package qyoga.programs

import qyoga.exercises.ExerciseId
import qyoga.exercises.ExercisesService
import qyoga.images.ImagesService
import java.io.InputStream


class ProgramsModule(
    val exercises: ExercisesService,
    val images: ImagesService
) {

    private val programs = HashMap<ProgramId, InputStream>()

    fun generateProgram(title: String, programExerciseIds: List<ExerciseId>): ProgramId {
        val programExercises = exercises.fetch(programExerciseIds)
        val id = ProgramId(programs.size.toLong())
        programs[id] = generateProgramPoi(Program(title, programExercises), images::fetch)
        return id
    }

    fun getProgram(id: ProgramId): InputStream? {
        return programs.remove(id)
    }

}