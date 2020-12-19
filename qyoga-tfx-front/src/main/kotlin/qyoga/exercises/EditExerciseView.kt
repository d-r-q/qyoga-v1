package qyoga.exercises

import io.ktor.http.*
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import kotlinx.coroutines.*
import qyoga.api.exercises.ExerciseEditDto
import qyoga.api.exercises.Tag
import qyoga.components.boundedImageChooser
import tornadofx.*
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

data class EditExerciseViewScope(val exercise: ExerciseEditDto) : Scope()

class SavingDialog : Fragment() {

    override val root = borderpane {
        paddingAll = 15.0
        center = label("Упражнение сохраняется") {
            alignment = Pos.CENTER
        }
    }

}

class EditExerciseView : View(), CoroutineScope by MainScope() {

    override val scope = super.scope as EditExerciseViewScope

    private val viewModel = EditExerciseViewModel(newEditExerciseModel)

    private val exercises: Exercises by di()

    override val root = form {
        paddingAll = 35.0
        spacing = 35.0
        vbox {
            vgrow = Priority.ALWAYS
            spacing = 35.0
            textfield {
                prefWidth = 450.0
                maxWidth = 450.0
                promptText = "Название"
                bind(viewModel.name)
            }
            hbox {
                spacing = 35.0
                vbox {
                    spacing = 35.0
                    boundedImageChooser(bound = 300.0) {
                        bindStringProperty(imgUrlProperty, null, null, viewModel.image1, false)
                    }
                    boundedImageChooser(bound = 300.0) {
                        bindStringProperty(imgUrlProperty, null, null, viewModel.image2, false)
                    }
                }
                vbox {
                    spacing = 35.0
                    hgrow = Priority.ALWAYS
                    textarea {
                        prefWidth = 225.0
                        promptText = "Описание"
                        isWrapText = true
                        bind(viewModel.descr)
                    }
                    textarea {
                        prefWidth = 225.0
                        promptText = "Инструкция"
                        isWrapText = true
                        bind(viewModel.instructions)
                    }
                    hbox {
                        spacing = 10.0
                        alignment = Pos.BASELINE_LEFT
                        label("Длительность:")
                        textfield {
                            maxWidth = 50.0
                            filterInput { it.controlNewText.isInt() && it.controlNewText.length <= 3 }
                            bind(viewModel.duration)
                        }
                        label("секунд")
                    }
                }
            }
            textfield {
                prefWidth = 450.0
                maxWidth = 450.0
                promptText = "Тэги: стопы, грудной отдел, вытяжение широчайшей"
                bind(viewModel.tags, converter = TagsToString)
            }
            hbox {
                vgrow = Priority.ALWAYS
                alignment = Pos.BOTTOM_RIGHT
                button("Отменить") {
                    action {
                        returnToDashboard()
                    }
                }
                spacer()
                button("Сохранить") {
                    action {
                        saveExercise()
                    }
                }
            }
        }
    }

    private fun saveExercise() {
        launch {
            val saveJob = async {
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
            val modalJob = async { find<SavingDialog>().openModal(escapeClosesWindow = false) }
            val saveRes =
                try {
                    withTimeoutOrNull(5.toDuration(TimeUnit.SECONDS)) {
                        saveJob.await()
                    }
                } finally {
                    modalJob.await()?.close()
                }
            if (saveRes == null) {
                error("Ошибка", "Упражнение не сохранено")
                return@launch
            }
            returnToDashboard()
        }
    }

    private fun returnToDashboard() {
        replaceWith<ExercisesDashboardView>(ViewTransition.Explode(0.5.seconds))
    }

    override fun onDock() {
        super.onDock()
        viewModel.setupFrom(scope.exercise, exercises::imageUrls)
    }

}

class EditExerciseViewModel(var dest: ExerciseEditDto) : ViewModel() {

    private var id: Long? = null
    val name = stringProperty(dest.name)
    val descr = stringProperty(dest.description)
    val instructions = stringProperty(dest.instructions)
    val duration = intProperty(dest.duration.seconds.toInt())
    val tags = listProperty(dest.tags.asObservable())
    val image1 = stringProperty(null)
    val image2 = stringProperty(null)

    fun setupFrom(dest: ExerciseEditDto, resolveImageUrls: ExerciseEditDto.() -> List<String>) {
        this.dest = dest
        id = dest.id
        name.set(dest.name)
        descr.set(dest.description)
        instructions.set(dest.instructions)
        duration.set(dest.duration.seconds.toInt())
        tags.set(dest.tags.asObservable())
        val imageUrls = resolveImageUrls(dest)
        image1.set(imageUrls.getOrNull(0))
        image2.set(imageUrls.getOrNull(1))
    }

    fun toDto(): ExerciseEditDto {
        return ExerciseEditDto(
            id,
            name.get(),
            descr.get(),
            instructions.get(),
            Duration.ofSeconds(duration.get().toLong()),
            tags.get(),
            emptyList()
        )
    }

    fun images(): List<Img> {
        return listOfNotNull(
            image1.get()?.let { if (it.startsWith("http")) ImageId(dest.images[0]) else ImageUrl(it) },
            image2.get()?.let { if (it.startsWith("http")) ImageId(dest.images[1]) else ImageUrl(it) }
        )
    }
}

val newEditExerciseModel = ExerciseEditDto(null, "", "", "", Duration.ZERO, emptyList(), emptyList())

object TagsToString : StringConverter<ObservableList<Tag>>() {

    override fun fromString(string: String?): ObservableList<Tag>? {
        if (string == null) {
            return null
        }
        return string.split(",")
                .map { Tag(it.trim().removePrefix("#")) }
                .filter { it.tag.isNotEmpty() }
                .asObservable()
    }

    override fun toString(lst: ObservableList<Tag>?): String {
        return lst?.joinToString(", ") ?: ""
    }
}