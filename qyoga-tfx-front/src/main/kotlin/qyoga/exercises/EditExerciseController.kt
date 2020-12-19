package qyoga.exercises

import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import tornadofx.Controller
import tornadofx.UIComponent
import tornadofx.ViewTransition
import tornadofx.seconds
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration


class EditExerciseController : Controller() {

    private val exercises: Exercises by di()

    fun cancel(): UIComponent.() -> Unit {
        return { returnToDashboard() }
    }

    suspend fun saveExercise(scope: CoroutineScope, viewModel: EditExerciseViewModel): UIComponent.() -> Unit {
        val saveJob = scope.async {
            val imageIds = viewModel.images().map {
                when (it) {
                    is ImageUrl -> {
                        val file = File(URI(it.url))
                        exercises.upload(
                            file.name,
                            ContentType.parse(Files.probeContentType(file.toPath())),
                            file
                        )
                    }
                    is ImageId -> {
                        it.id
                    }
                }
            }
            val res = exercises.send(viewModel.toDto().copy(images = imageIds))
            println("Got $res")
            res
        }
        val modalJob = scope.async { find<SavingDialog>().openModal(escapeClosesWindow = false) }
        val saveRes =
            try {
                withTimeoutOrNull(5.toDuration(TimeUnit.SECONDS)) {
                    saveJob.await()
                }
            } finally {
                modalJob.await()?.close()
            }
        if (saveRes == null) {
            tornadofx.error("Ошибка", "Упражнение не сохранено")
            return { }
        }
        return { returnToDashboard() }
    }

    fun UIComponent.returnToDashboard() {
        replaceWith<ExercisesDashboardView>(ViewTransition.Explode(0.5.seconds))
    }

}