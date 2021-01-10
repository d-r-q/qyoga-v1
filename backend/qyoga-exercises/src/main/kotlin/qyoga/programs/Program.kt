package qyoga.programs

import qyoga.api.exercises.ExerciseEditDto
import qyoga.domain.DomainId

data class ProgramId(override val value: Long) : DomainId<Long, Program>

data class Program(
    val title: String,
    val exercises: List<ExerciseEditDto>
)