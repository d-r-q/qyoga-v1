package qyoga.api.exercises

import java.time.Duration

data class Tag(val tag: String) {

    override fun toString() = "#$tag"
}

data class ExerciseEditDto(
    val id: Long?,
    val name: String,
    val annotation: String,
    val instructions: String,
    val duration: Duration,
    val tags: List<Tag>,
    val images: List<String>
)