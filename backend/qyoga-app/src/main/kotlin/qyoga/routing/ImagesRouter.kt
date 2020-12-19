package qyoga.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import qyoga.files.Image
import qyoga.files.ImagesModule
import qyoga.web.respond

object ImagesRouter {

    fun Routing.images(imagesModule: ImagesModule) {
        route("images") {
            get("/{id}") {
                val id: Long by call.parameters
                val file = imagesModule.imagesService.fetch(id).onError {
                    respond(it)
                    return@get
                }
                if (file == null) {
                    call.respond(HttpStatusCode.NoContent, "Image with id $id not found")
                    return@get
                }

                call.respondBytes(ContentType.parse(file.mimeType), HttpStatusCode.OK) {
                    file.content
                }
            }

            post {
                val multipart = call.receiveMultipart()
                val img = multipart.readPart() as? PartData.FileItem
                if (img == null) {
                    call.respond(HttpStatusCode.BadRequest, "No file content")
                    return@post
                }
                val originalFileName = img.originalFileName

                if (originalFileName == null) {
                    call.respond(HttpStatusCode.BadRequest, "No file name")
                    return@post
                }
                val res = imagesModule.imagesService.save(
                    Image(originalFileName, img.contentType.toString(), img.provider().readBytes())
                )

                respond(res)
            }
        }
    }

}