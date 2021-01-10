package qyoga.programs

import org.docx4j.jaxb.Context
import org.docx4j.model.structure.PageDimensions
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage
import org.docx4j.wml.*
import qyoga.Outcome
import qyoga.images.Image
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger


private val of: ObjectFactory = Context.getWmlObjectFactory()

fun generateProgram(program: Program, fetchImages: (List<Long>) -> Outcome<List<Image>>): InputStream {
    val wordMLPackage = WordprocessingMLPackage.createPackage()
    val mainDocumentPart = wordMLPackage.mainDocumentPart

    val body = mainDocumentPart.jaxbElement.body
    val page = PageDimensions()
    val pgMar = page.pgMar
    pgMar.left = BigInteger("800")
    pgMar.right = BigInteger("800")
    val sectPr: SectPr = of.createSectPr()
    body.sectPr = sectPr
    sectPr.pgMar = pgMar

    mainDocumentPart.addStyledParagraphOfText("Title", program.title)

    val tbl = of.createTbl()
    tbl.tblGrid = TblGrid().apply {
        gridCol.addAll(
            listOf(
                TblGridCol().apply { w = BigInteger("3000") },
                TblGridCol().apply { w = BigInteger("7300") })
        )
    }
    for (ex in program.exercises) {
        val titleRow = of.createTr()
        val titleCell = of.createTc()
        titleCell.tcPr =
            of.createTcPr().apply { gridSpan = TcPrInner.GridSpan().apply { this.`val` = BigInteger("2") } }
        titleCell.content.addAll(createParagraphs(of, ex.name) {
            it.rPr = of.createRPr().apply { b = BooleanDefaultTrue() }
        })
        titleCell.content.addAll(createParagraphs(of, ex.description))
        titleRow.content.add(titleCell)

        tbl.content.add(titleRow)

        val instructionsRow = of.createTr()
        val imagesCell = of.createTc()

        var id = 0
        fetchImages(ex.images).onSuccess { images ->
            for (img in images) {
                val img1 = BinaryPartAbstractImage.createImagePart(wordMLPackage, img.content)
                val inline = img1.createImageInline("hint $id", "alt $id", id++, id, false)
                val d = of.createDrawing()
                d.anchorOrInline.add(inline)
                val p = of.createP()
                p.content.add(d)
                imagesCell.content.add(p)
            }
        }
        instructionsRow.content.add(imagesCell)

        val instructionsCell = of.createTc()
        instructionsCell.content.addAll(createParagraphs(of, ex.instructions))
        instructionsRow.content.add(instructionsCell)

        tbl.content.add(instructionsRow)
    }

    mainDocumentPart.addObject(tbl)

    val buffer = ByteArrayOutputStream()
    wordMLPackage.save(buffer)
    return ByteArrayInputStream(buffer.toByteArray())
}

private fun createParagraphs(of: ObjectFactory, text: String, cfg: (R) -> Unit = {}): List<P> {
    return text.split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map {
            val p = of.createP()
            val r = of.createR()
            val t = of.createText()
            t.value = it
            r.content.add(t)
            p.content.add(r)
            cfg(r)
            p
        }
}
