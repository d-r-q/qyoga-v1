package qyoga

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.routing.*
import org.slf4j.event.Level
import qyoga.db.DbModule
import qyoga.exercises.ExercisesCrudModule
import qyoga.images.ImagesModule
import qyoga.programs.ProgramsModule
import qyoga.routing.ExercisesCrudRouter.exercises
import qyoga.routing.ImagesRouter.images
import qyoga.routing.ProgramsRouter.programs

class QyogaModule(env: QEnv) {

    val dbModule = DbModule(env)
    val imagesModule = ImagesModule(dbModule)
    val exercisesModule = ExercisesCrudModule(dbModule)
    val programsModule = ProgramsModule(exercisesModule.exercisesService, imagesModule.imagesService)

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
    install(CallLogging) {
        level = Level.TRACE
    }
    install(Routing) {
        exercises(qyoga.exercisesModule)
        images(qyoga.imagesModule)
        programs(qyoga.programsModule)
    }

}
