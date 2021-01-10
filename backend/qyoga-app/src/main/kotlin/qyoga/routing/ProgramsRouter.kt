package qyoga.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.utils.io.jvm.javaio.*
import qyoga.NotFound
import qyoga.Ok
import qyoga.exercises.ExerciseId
import qyoga.programs.ProgramId
import qyoga.programs.ProgramsModule
import qyoga.web.respond

data class GenerateProgramRequest(
    val title: String,
    val exercises: List<Long>
)

object ProgramsRouter {

    fun Routing.programs(programsModule: ProgramsModule) {
        route("program") {
            post {
                val req = call.receive<GenerateProgramRequest>()
                val id = programsModule.generateProgram(req.title, req.exercises.map(::ExerciseId))
                respond(Ok(id))
            }
            get("/{id}") {
                val id: Long by call.parameters
                val dId = ProgramId(id)
                val docx = programsModule.getProgram(dId)
                if (docx == null) {
                    respond(NotFound(dId))
                    return@get
                }
                call.respondBytesWriter(ContentType.defaultForFileExtension("docx")) {
                    docx.copyTo(this.toOutputStream())
                }
            }
        }
    }

}