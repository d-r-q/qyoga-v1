package qyoga.routing

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import qyoga.Ok
import qyoga.api.exercises.ExerciseEditDto
import qyoga.exercises.ExercisesCrudModule
import qyoga.exercises.ExercisesPage
import qyoga.web.respond

object ExercisesCrudRouter {

    fun Routing.exercises(exercisesCrudModule: ExercisesCrudModule) {
        route("/exercises") {
            get {
                val sinceName = call.request.queryParameters["sinceName"] ?: "A"
                val amount = call.request.queryParameters["amount"]?.toIntOrNull() ?: 10
                val res = exercisesCrudModule.exercisesService.getPage(ExercisesPage(sinceName, amount))
                call.respond(res)
            }

            post {
                val exercise = call.receive<ExerciseEditDto>()
                val res = exercisesCrudModule.exercisesService.persistExercise(exercise)
                respond(res)
            }

            route("/{exerciseId}") {
                put {
                    val exerciseId: Long by call.parameters
                    val exercise = call.receive<ExerciseEditDto>()
                    require(exerciseId == exercise.id)
                    val res = exercisesCrudModule.exercisesService.persistExercise(exercise)
                    respond(res.flatMap { Ok })
                }
            }

        }
    }

}