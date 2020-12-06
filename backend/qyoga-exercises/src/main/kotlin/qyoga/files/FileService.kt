package qyoga.files

import qyoga.db.DbModule

class FileService(val dbModule: DbModule) {
    fun fetchFile(id: Long): QFile? {
        dbModule.ebeanDb.beginTransaction().use {
            it.isReadOnly = true
            with(it.connection) {
                val stmt = prepareStatement("SELECT name, content_type, content FROM images WHERE id = ?").apply {
                    setLong(1, id)
                }
                val rs = stmt.executeQuery()
                val found = rs.next()
                if (!found) {
                    return null
                }

                return QFile(rs.getString("name"), rs.getString("content_type"), rs.getBytes("content"))
            }
        }
    }

}
