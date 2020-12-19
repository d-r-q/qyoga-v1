package qyoga.exercises

import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import qyoga.api.exercises.ExerciseEditDto
import qyoga.components.boundedImage
import qyoga.components.noSelectionModel
import tornadofx.*

class ExercisesDashboardView : View(), CoroutineScope by MainScope() {

    private val exercises: Exercises by di()

    lateinit var exerciseListView: ListView<ExerciseEditDto>

    override val root = borderpane {
        center = vbox {
            paddingAll = 15.0
            borderpane {
                paddingAll = 15.0
                left = button("Добавить") {
                    action {
                        val scope = EditExerciseViewScope(newEditExerciseModel)
                        val editView = find<EditExerciseView>(scope)
                        replaceWith(editView, ViewTransition.Explode(0.5.seconds))
                    }
                }
                right = checkbox()
            }
            exerciseListView = listview {
                vgrow = Priority.ALWAYS
                selectionModel = noSelectionModel()
                cellFormat {
                    exerciseCellFormat(it)
                }
            }
        }
        right = borderpane {
            top = vbox {
                paddingAll = 15.0
                spacing = 5.0

                label("Поиск по тегу")
                combobox<String> {
                    prefWidth = 300.0
                    items = FXCollections.observableArrayList("tag1", "tag2", "tag3")
                }

                spacer()
                label("Поиск по названию")
                textfield()

                spacer()
                button("Искать") {
                    maxWidth = Double.MAX_VALUE
                }
            }

            bottom = vbox {
                paddingAll = 15.0
                spacing = 10.0
                isFillWidth = true

                label("Заголовок")
                textfield()
                button("Скачать") {
                    maxWidth = Double.MAX_VALUE
                }
            }
        }
    }

    private fun ListCell<ExerciseEditDto>.exerciseCellFormat(it: ExerciseEditDto) {
        border =
            Border(
                BorderStroke(
                    Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT
                )
            )
        graphic =
            hbox {
                paddingAll = 10.0
                hbox {
                    hgrow = Priority.ALWAYS
                    spacing = 15.0

                    val url = exercises.imageUrls(it).firstOrNull()
                    boundedImage(url?.let { Image(it) }, 300.0)

                    vbox {
                        hgrow = Priority.ALWAYS
                        label(it.name)
                        textarea(it.description.trimIndent()) {
                            isWrapText = true
                        }
                    }
                }
                vbox {
                    paddingAll = 15.0
                    prefWidth = 150.0
                    spacing = 10.0

                    it.tags.forEach {
                        label(it.toString())
                    }
                }
                vbox {
                    alignment = Pos.BOTTOM_CENTER
                    paddingAll = 15.0
                    prefWidth = 50.0
                    spacing = 10.0

                    checkbox()
                    hbox {
                        paddingAll = 5.0
                        spacing = 10.0
                        button("\uD83D\uDD89") {
                            action {
                                val scope = EditExerciseViewScope(it)
                                val editView = find<EditExerciseView>(scope)
                                replaceWith(editView, ViewTransition.Explode(0.5.seconds))
                            }
                        }
                        button("\uD83D\uDDD1")
                    }
                }
            }
    }


    override fun onDock() {
        launch(Dispatchers.JavaFx) {
            try {
                val items = exercises.fetch()
                with(exerciseListView.items) {
                    clear()
                    addAll(items)
                }
            } catch (e: Exception) {
                error("Ошибка", "Не удалось загрузить список упражнений")
            }
        }
    }

}