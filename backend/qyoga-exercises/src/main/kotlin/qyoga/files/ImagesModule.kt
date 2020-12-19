package qyoga.files

import qyoga.db.DbModule

class ImagesModule(dbModule: DbModule) {

    val imagesService = ImagesService(dbModule)

}
