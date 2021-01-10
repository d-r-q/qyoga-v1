package qyoga.images

import qyoga.db.DbModule

class ImagesModule(dbModule: DbModule) {

    val imagesService = ImagesService(dbModule)

}
