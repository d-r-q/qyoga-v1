import javafx.collections.FXCollections
import javafx.collections.FXCollections.emptyObservableList
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import qyoga.api.exercises.ExerciseEditDto
import tornadofx.*


class AddExercise : View() {

    override val root = form {
        button("Сохранить") {
            action {
                replaceWith<HelloWorld>(ViewTransition.Explode(0.5.seconds))
            }
        }
    }

}

class HelloWorld : View(), CoroutineScope by MainScope() {

    lateinit var lv: ListView<ExerciseEditDto>

    override val root = borderpane {
        center = vbox {
            paddingAll = 15.0
            borderpane {
                paddingAll = 15.0
                left = button("Добавить") {
                    action {
                        replaceWith<AddExercise>(ViewTransition.Explode(0.5.seconds))
                    }
                }
                right = checkbox()
            }
            lv = listview {
                vgrow = Priority.ALWAYS
                selectionModel = object : MultipleSelectionModel<ExerciseEditDto?>() {
                    override fun clearAndSelect(index: Int) {}
                    override fun select(index: Int) {}
                    override fun select(obj: ExerciseEditDto?) {}
                    override fun clearSelection(index: Int) {}
                    override fun clearSelection() {}
                    override fun isSelected(index: Int) = false
                    override fun isEmpty() = true
                    override fun selectPrevious() {}
                    override fun selectNext() {}
                    override fun selectFirst() {}
                    override fun selectLast() {}
                    override fun getSelectedIndices(): ObservableList<Int> = emptyObservableList()
                    override fun getSelectedItems(): ObservableList<ExerciseEditDto?> = emptyObservableList()
                    override fun selectIndices(index: Int, vararg indices: Int) {}
                    override fun selectAll() {}
                }
                exerciseItem()
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

    private fun ListView<ExerciseEditDto>.exerciseItem() {
        cellFormat {
            border =
                Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
            graphic =
                hbox {
                    paddingAll = 10.0
                    hbox {
                        hgrow = Priority.ALWAYS
                        spacing = 15.0

                        val imgUrl = it.images.firstOrNull()
                            ?: "file:///home/azhidkov/0my/Alive/qyoga/qg-repo-master/qyoga-tfx-front/src/main/resources/proxy-image.jpg"
                        val img = Image(imgUrl)
                        val left = region {
                            val iv = imageview(img) {
                                alignment = Pos.CENTER
                            }

                            if (img.width > 300.0) {
                                iv.fitWidth = 300.0
                                iv.isSmooth = true
                                iv.isPreserveRatio = true
                            } else {
                                val padding = (300.0 - img.width) / 2
                                hboxConstraints {
                                    marginLeft = padding
                                    marginRight = padding
                                }
                            }
                        }
                        left.maxWidth(300.0)

                        vbox {
                            hgrow = Priority.ALWAYS
                            label(it.name)
                            textarea(it.annotation.trimIndent()) {
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
                            button("\uD83D\uDD89")
                            button("\uD83D\uDDD1")
                        }
                    }
                }
        }
    }

    init {
        GlobalScope.launch(Dispatchers.JavaFx) {
            val items = Exercises().fetch()
            with(lv.items) {
                clear()
                addAll(items)
            }
        }
    }

}

class QyogaTfxApp : App(HelloWorld::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1920.0
        stage.height = 1080.0
        stage.x = (Screen.getPrimary().visualBounds.width - 1920.0) / 2
        stage.y = (Screen.getPrimary().visualBounds.height - 1080.0) / 2
    }
}