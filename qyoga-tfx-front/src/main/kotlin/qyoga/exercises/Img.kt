package qyoga.exercises


sealed class Img

class ImageUrl(val url: String) : Img()

class ImageId(val id: Long) : Img()
