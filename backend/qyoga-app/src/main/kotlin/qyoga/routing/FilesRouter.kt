package qyoga.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import qyoga.files.FilesModule

object FilesRouter {

    fun mount(filesModule: FilesModule): Routing.() -> Unit = {
        route("images/{id}") {
            get {
                val id: Long by call.parameters
                val file = filesModule.filesService.fetchFile(id)
                if (file == null) {
                    call.respond(HttpStatusCode.NoContent, "Image with id $id not found")
                    return@get
                }

                call.respondBytes(ContentType.parse(file.mimeType), HttpStatusCode.OK) {
                    file.content
                }
            }
        }
    }

}