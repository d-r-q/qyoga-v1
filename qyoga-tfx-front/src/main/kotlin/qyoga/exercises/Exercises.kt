package qyoga.exercises

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.ContentType
import io.ktor.http.contentType
import qyoga.api.exercises.ExerciseEditDto
import tornadofx.*

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

    suspend fun send(exercise: ExerciseEditDto): Boolean {
        return if (exercise.id == null) {
            client.post<Any>("$baseUrl/exercises") {
                body = exercise
            }
            true
        } else {
            client.put<Any>("$baseUrl/exercises/${exercise.id}") {
                body = exercise
            }
            true
        }
    }

}