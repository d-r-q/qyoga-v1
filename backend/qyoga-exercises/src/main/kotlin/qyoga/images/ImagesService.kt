package qyoga.images

import qyoga.*
import qyoga.db.DbModule
import qyoga.db.asSequence
import qyoga.domain.DomainId
import java.sql.Statement

data class ImageId(override val value: Long) : DomainId<Long, Image>

class ImagesService(private val dbModule: DbModule) {

    fun fetch(id: Long): Outcome<Image> = dbModule.transaction {
        it.isReadOnly = true
        with(it.connection) {
            val stmt = prepareStatement("SELECT id, name, content_type, content FROM images WHERE id = ?").apply {
                setLong(1, id)
            }
            val rs = stmt.executeQuery()
            val found = rs.next()
            if (!found) {
                return@transaction NotFound(ImageId(id))
            }

            Ok(Image(rs.getString("name"), rs.getString("content_type"), rs.getBytes("content"), rs.getLong("id")))
        }
    }

    fun fetch(ids: List<Long>): List<Image> {
        val res = dbModule.transaction { trx ->
            trx.isReadOnly = true
            with(trx.connection) {
                val stmt = prepareStatement(
                    "SELECT id, name, content_type, content FROM images WHERE id IN (${
                        ids.map { "?" }.joinToString(", ")
                    })"
                ).apply {
                    ids.forEachIndexed { index, id -> setLong(index + 1, id) }
                }
                val imgs = stmt.executeQuery().asSequence()
                    .map { Image(it["name"], it["content_type"], it["content"], it["id"]) }
                    .toList()

                Ok(imgs)
            }
        }
        return res.onErrorThrow()
    }

    fun save(img: Image): Outcome<Long> = dbModule.transaction {
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
            Failure(e, "Image persisting failed")
        }
    }

}
