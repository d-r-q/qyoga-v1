package qyoga.programs

import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.Document
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignH
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar.Factory
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr
import qyoga.Ok
import qyoga.Outcome
import qyoga.api.exercises.ExerciseEditDto
import qyoga.images.Image
import java.io.*
import java.math.BigInteger
import java.time.Duration
import javax.imageio.ImageIO


fun generateProgramPoi(program: Program, fetchImages: (List<Long>) -> Outcome<List<Image>>): InputStream {
    val buff = ByteArrayOutputStream()
    XWPFDocument().use {
        it.document.body.sectPr = CTSectPr.Factory.newInstance().apply {
            pgMar = Factory.newInstance().apply {
                left = BigInteger("800")
                right = BigInteger("800")
            }
        }
        with(it.createParagraph()) {
            with(createRun()) {
                setText(program.title)
                fontSize = 24
            }
        }

        program.exercises
            .forEach { ex ->
                with(it.createParagraph()) {
                    addRun(createRun().apply { this.setText(ex.name); isBold = true })
                }

                with(it.createParagraph()) {
                    addRun(createRun().apply { this.setText(ex.description) })
                }

                with(it.createParagraph()) {
                    fetchImages(ex.images).onSuccess { images ->
                        images
                            .filter { it.typeOrNull() != null }
                            .forEach { img ->
                                addRun(createRun().apply {
                                    val anch = CTAnchor.Factory.newInstance().apply {
                                        addNewPositionH().align = STAlignH.LEFT
                                    }
                                    anch.addNewGraphic().apply {
                                        addNewGraphicData().apply {
                                            val (width, height) = img.dimensions()
                                            addPicture(
                                                ByteArrayInputStream(img.content),
                                                img.typeOrNull()!!,
                                                img.name,
                                                Units.toEMU(300.0),
                                                Units.toEMU(height.toDouble())
                                            )
                                        }
                                    }
                                    ctr.drawingList[0].anchorList.add(anch)
                                })
                            }
                    }
                    addRun(createRun().apply { this.setText(ex.instructions) })
                }
            }

        it.write(buff)
    }
    return buff.toByteArray().inputStream()
}

fun Image.typeOrNull() =
    when (this.mimeType) {
        "image/png" -> Document.PICTURE_TYPE_PNG
        "image/jpeg" -> Document.PICTURE_TYPE_JPEG
        else -> null
    }

fun Image.dimensions(): Pair<Int, Int> = with(ImageIO.read(ByteArrayInputStream(this.content))) {
    width to height
}

fun main() {
    val img = Image("name", "image/png", FileInputStream("/home/azhidkov/Downloads/image32.png").readBytes())
    val ins = generateProgramPoi(
        Program(
            "Программа занятия 01.01.2021", listOf(
                ExerciseEditDto(
                    null,
                    "Цикл маджариасаны (кошка - корова) сагитальная плоскость",
                    """Цикл маджариасаны улучшает мобильность позвоночника, снимает напряжение с паравертебральных мышц, служит разогревом перед более сложными движениями в позвоночнике.""",
                    """На вдохе направляем копчик вверх ➔ прогибаем поясницу ➔ прогибаем грудной отдел позвоночника ➔ смотрим чуть выше линии горизонта, но не закидываем голову назад.
Особое внимание на прогиб в пояснице.

На выдохе копчик направляем вниз ➔ округляем поясницу ➔ округляем грудной отдел позвоночника, выталкиваем пространство между лопаток вверх, руками толкаем пол от себя ➔ голова опускается вниз
Выполняем на медленном дыхании, 10 дыхательных циклов.
""",
                    Duration.ZERO,
                    emptyList(),
                    emptyList()
                ),

                ExerciseEditDto(
                    null,
                    "Цикл маджариасаны (кошка - корова) сагитальная плоскость2",
                    """2Цикл маджариасаны улучшает мобильность позвоночника, снимает напряжение с паравертебральных мышц, служит разогревом перед более сложными движениями в позвоночнике.""",
                    """На вдохе направляем копчик вверх ➔ прогибаем поясницу ➔ прогибаем грудной отдел позвоночника ➔ смотрим чуть выше линии горизонта, но не закидываем голову назад.
Особое внимание на прогиб в пояснице.

На выдохе копчик направляем вниз ➔ округляем поясницу ➔ округляем грудной отдел позвоночника, выталкиваем пространство между лопаток вверх, руками толкаем пол от себя ➔ голова опускается вниз
Выполняем на медленном дыхании, 10 дыхательных циклов.
""",
                    Duration.ZERO,
                    emptyList(),
                    emptyList()
                )
            )
        )
    ) { Ok(listOf(img, img)) }
    val ous = FileOutputStream("./tmp.docx")
    ins.copyTo(ous)
}