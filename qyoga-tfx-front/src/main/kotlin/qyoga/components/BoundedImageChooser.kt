package qyoga.components

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import tornadofx.*

data class BoundedImageChooserScope(
    val img: Image?,
    val bound: Double
) : Scope()

inline fun EventTarget.boundedImageChooser(
    img: Image? = null,
    bound: Double,
    op: BoundedImageChooser.() -> Unit = {}
): BoundedImageChooser {
    val boundedImage = find<BoundedImageChooser>(BoundedImageChooserScope(img, bound)).apply(op)
    this += boundedImage
    return boundedImage
}

class BoundedImageChooser : Fragment("Bounded Image Chooser") {

    override val scope = super.scope as BoundedImageChooserScope

    override val root =
        stackpane {
            boundedImage(scope.img, scope.bound) {
                onLeftClick {
                    val choose = chooseFile(
                        "Выберите изображение",
                        arrayOf(FileChooser.ExtensionFilter("Изображения", "*.png")),
                        owner = currentWindow
                    )
                    val imgUrl = choose.firstOrNull()?.toURI()?.toURL()?.toString()
                        ?: return@onLeftClick

                    onImageSelected(imgUrl)
                }
            }

            alignment = Pos.TOP_RIGHT
            button("-") {
                action {
                    clearSelection()
                }
            }
        }


    val imgUrlProperty = stringProperty(null).apply {
        val imageView = ((root.children[0] as BorderPane).center as ImageView)
        addListener { _, _, newValue -> imageView.image = Image(newValue ?: imagePlaceholderUrl.toString()) }
    }

    private fun onImageSelected(url: String) {
        imgUrlProperty.set(url)
    }

    private fun clearSelection() {
        imgUrlProperty.set(null)
    }

}