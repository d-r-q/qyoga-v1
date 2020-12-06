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

            get("{exerciseId}/images/{idx}") {
                val exerciseId: Long by call.parameters
                val idx: Int by call.parameters
                val file = exercisesCrudModule.exercisesService.fetchFile(exerciseId, idx)
                if (file == null) {
                    call.respond(HttpStatusCode.NoContent, "Image for exercise $exerciseId with idx $idx not found")
                    return@get
                }

                call.respondBytes(ContentType.parse(file.mimeType), HttpStatusCode.OK) {
                    file.content
                }

            }

            post {
                val exercise = call.receive<ExerciseEditDto>()
                val res = exercisesCrudModule.exercisesService.createExercise(exercise)
                call.respond(res)
            }
        }
    }

}