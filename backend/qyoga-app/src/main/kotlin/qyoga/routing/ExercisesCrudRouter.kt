package qyoga.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import qyoga.api.exercises.ExerciseEditDto
import qyoga.exercises.ExercisesCrudModule
import qyoga.exercises.ExercisesPage

object ExercisesCrudRouter {

    fun mount(exercisesCrudModule: ExercisesCrudModule): Routing.() -> Unit = {
        route("/exercises") {
            get {
                val sinceName = call.request.queryParameters.get("sinceName") ?: "A"
                val amount = call.request.queryParameters.get("amount")?.toIntOrNull() ?: 10
                val res = exercisesCrudModule.exercisesService.getPage(ExercisesPage(sinceName, amount))
                call.respond(res)
            }

            post {
                val exercise = call.receive<ExerciseEditDto>()
                val res = exercisesCrudModule.exercisesService.createExercise(exercise)
                call.respond(res)
            }

            put("/{exerciseId}") {
                val exerciseId: Long by call.parameters
                val exercise = call.receive<ExerciseEditDto>()
                require(exerciseId == exercise.id)
                exercisesCrudModule.exercisesService.updateExercise(exercise)
                call.respond(HttpStatusCode.OK, "")
            }
        }
    }

}