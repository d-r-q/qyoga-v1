package qyoga

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.routing.*
import qyoga.db.DbModule
import qyoga.exercises.ExercisesCrudModule
import qyoga.files.FilesModule
import qyoga.routing.ExercisesCrudRouter
import qyoga.routing.FilesRouter

class QyogaModule(env: QEnv) {

    val dbModule = DbModule(env)
    val filesModule = FilesModule(dbModule)
    val exercisesModule = ExercisesCrudModule(dbModule, filesModule)

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
        ExercisesCrudRouter.mount(qyoga.exercisesModule)()
        FilesRouter.mount(qyoga.filesModule)()
    }
}
