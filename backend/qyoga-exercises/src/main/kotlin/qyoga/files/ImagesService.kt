package qyoga.files

import qyoga.*
import qyoga.db.DbModule
import qyoga.domain.DomainId
import java.sql.Statement

data class ImageId(override val value: Long) : DomainId<Long, Image>

class ImagesService(private val dbModule: DbModule) {

    fun fetch(id: Long): Outcome<Image?, Exception> = dbModule.transaction {
        it.isReadOnly = true
        with(it.connection) {
            val stmt = prepareStatement("SELECT name, content_type, content FROM images WHERE id = ?").apply {
                setLong(1, id)
            }
            val rs = stmt.executeQuery()
            val found = rs.next()
            if (!found) {
                return@transaction NotFound(ImageId(id))
            }

            Ok(Image(rs.getString("name"), rs.getString("content_type"), rs.getBytes("content")))
        }
    }

    fun save(img: Image): Outcome<Long, Exception> = dbModule.transaction {
        try {
            val stmt = it.connection.prepareStatement(
                "INSERT INTO images (name, content_type, content) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS
            ).apply {
                setString(1, img.name)
                setString(2, img.mimeType)
                setBytes(3, img.content)
            }
            stmt.execute()
            val id = with(stmt.generatedKeys) {
                next()
                getLong(1)
            }
            Success(id)
        } catch (e: Exception) {
            Failure("Image persisting failed", e)
        }
    }

}
