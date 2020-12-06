package qyoga.exercises

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import qyoga.api.exercises.ExerciseEditDto
import tornadofx.ConfigProperties

class Exercises(config: ConfigProperties) {

    private val baseUrl = config["qyoga.client.baseUrl"]

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                this.registerModule(JavaTimeModule())
            }
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun fetch(): List<ExerciseEditDto> {
        return client.get("$baseUrl/exercises")
    }

    suspend fun create(exercise: ExerciseEditDto): ExerciseEditDto? {
        return client.post("$baseUrl/exercises") {
            body = exercise
        }
    }

}