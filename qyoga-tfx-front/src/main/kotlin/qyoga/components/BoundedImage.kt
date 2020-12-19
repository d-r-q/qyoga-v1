package qyoga.components

import javafx.event.EventTarget
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import qyoga.QyogaTfxApp
import tornadofx.borderpane
import tornadofx.imageview
import tornadofx.onChange

internal val imagePlaceholderUrl = QyogaTfxApp::class.java.getResource("/img/image-placeholder.jpg")
internal val imagePlaceholder = Image(imagePlaceholderUrl.toString())

fun EventTarget.boundedImage(img: Image?, bound: Double, cfg: EventTarget.() -> Unit = {}) = borderpane {
    fun ImageView.rebound() {
        fitWidth = if (image.width > bound) {
            bound
        } else {
            -1.0
        }
    }

    minWidth = bound
    center = imageview(img ?: imagePlaceholder) {
        isPreserveRatio = true
    }
    val view = center as ImageView
    view.rebound()
    view.imageProperty().onChange {
        if (it != null) {
            view.rebound()
        } else {
            view.image = imagePlaceholder
        }
    }
}.apply(cfg)


