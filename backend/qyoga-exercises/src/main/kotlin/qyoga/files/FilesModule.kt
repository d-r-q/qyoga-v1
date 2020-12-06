package qyoga.files

import qyoga.db.DbModule

class FilesModule(dbModule: DbModule) {

    val filesService = FileService(dbModule)

}
