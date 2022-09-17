package qyoga.api.exercises

import java.time.Duration

data class Tag(val tag: String) {

    override fun toString() = "#$tag"

}

data class StepDto(
    val description: String,
    val imageId: Long?
) {

    fun imageUrl(baseUrl: String): String? = imageId?.let { "$baseUrl/images/$it" }

}

data class ExerciseEditDto(
    val id: Long?,
    val name: String,
    val description: String,
    val instructions: List<StepDto>,
    val duration: Duration,
    val tags: List<Tag>
) {

    fun renderInstructions(): String {
        var imageIdx = 1
        val stepTexts = instructions.map {
            ("img: ${it.imageId?.let { imageIdx++ } ?: "нет"}\n" +
                    "\n" +
                    it.description.trim()
                    )
        }
        return stepTexts.joinToString("\n\n---\n")
    }

    companion object {
        fun parseInstructions(instructionsStr: String, imageIds: List<Long>): List<StepDto> {
            return instructionsStr.split("---\n")
                .map {
                    val (imgLine, description) = with(it.lines()) {
                        this.first() to this.drop(1).joinToString("\n")
                    }

                    val imgIdx = ("img: (\\d+)").toRegex().matchEntire(imgLine)?.groupValues?.get(1)
                    val imgId = imgIdx?.let { imageIds[imgIdx.toInt() - 1] }
                    StepDto(description.trim(), imgId)
                }
        }
    }
}

fun ExerciseEditDto.images(): List<Long> =
    this.instructions.mapNotNull { it.imageId }