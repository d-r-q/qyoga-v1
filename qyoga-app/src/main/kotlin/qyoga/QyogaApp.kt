package qyoga

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import qyoga.ExercisesRouter.mount
import qyoga.db.DbModule
import qyoga.exercises.ExercisesModule
import qyoga.exercises.ExercisesPage

class QyogaModule(env: QEnv) {

    val dbModule = DbModule(env)
    val exercisesModule = ExercisesModule(dbModule)

}

fun Application.main() {
    val env = QEnv(environment.config)
    val qyoga = QyogaModule(env)
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
            registerModule(JavaTimeModule())  // support java.time.* types
        }
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        mount(qyoga.exercisesModule)
    }
}

object ExercisesRouter {

    fun Routing.mount(exercisesModule: ExercisesModule) {
        get("/exercises") {
            val sinceName = call.request.queryParameters.get("sinceName") ?: "A"
            val amount = call.request.queryParameters.get("amount")?.toIntOrNull() ?: 10
            val res = exercisesModule.exercisesService.getPage(ExercisesPage(sinceName, amount))
            call.respond(res)
        }
    }

}