package qyoga.exercises

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import qyoga.api.exercises.ExerciseEditDto
import tornadofx.ConfigProperties
import java.io.File

class Exercises(config: ConfigProperties) {

    private val baseUrl = config.string("qyoga.client.baseUrl")!!

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                this.registerModule(JavaTimeModule())
            }
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
    }

    suspend fun fetch(): List<ExerciseEditDto> {
        return client.get("$baseUrl/exercises")
    }

    suspend fun send(exercise: ExerciseEditDto): ExerciseEditDto? {
        return if (exercise.id == null) {
            val savedExercise = client.post<ExerciseEditDto>("$baseUrl/exercises") {
                body = exercise
            }
            savedExercise
        } else {
            val resp = client.put<HttpStatement>("$baseUrl/exercises/${exercise.id}") {
                body = exercise
            }
                .execute()

            if (!resp.status.isSuccess()) {
                return null
            }
            exercise
        }
    }

    suspend fun upload(name: String, contentType: ContentType, file: File): Long {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder()
                .build()

            val part: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
                "image",
                name,
                file.asRequestBody(contentType.toString().toMediaTypeOrNull())
            )
            val request = Request.Builder()
                .url("$baseUrl/images")
                .post(MultipartBody.Builder().addPart(part).build())
                .build()

            val res = client.newCall(request).execute()
            res.body!!.string().toLong()
        }
    }

    fun imageUrls(it: ExerciseEditDto): List<String> =
        it.instructions.mapNotNull { it.imageUrl(baseUrl) }

}