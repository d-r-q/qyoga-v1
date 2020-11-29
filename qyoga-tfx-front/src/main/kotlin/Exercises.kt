import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import qyoga.api.exercises.ExerciseEditDto

class Exercises {

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                this.registerModule(JavaTimeModule())
            }
        }
    }

    suspend fun fetch(): List<ExerciseEditDto> {
        return client.get("http://localhost:8090/exercises")
    }

}