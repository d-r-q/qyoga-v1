package qyoga.web

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory
import qyoga.*

val log = LoggerFactory.getLogger("qyoga.Web")

fun ErrorBody(failure: GenericFailure<*, Exception>) =
    ErrorBody(failure.message ?: "", failure.cause.stackTraceToString())

data class ErrorBody(
    val message: String,
    val stackTrace: String? = null
)

suspend fun PipelineContext<*, ApplicationCall>.respond(res: Outcome<Any, Exception>) {
    when (res) {
        is Ok -> {
            log.trace("Request {} succeed with {}", call.prettyPrint(), res)
            call.respond(HttpStatusCode.NoContent, "")
        }
        is NotFound -> {
            log.trace("Request {} failed with {}", call.prettyPrint(), res)
            call.respond(HttpStatusCode.NotFound, ErrorBody(message = res.cause.message))
        }
        is GenericSuccess -> {
            log.trace("Request {} succeed with {}", call.prettyPrint(), res.result)
            call.respond(HttpStatusCode.OK, res.result)
        }
        is GenericFailure<*, *> -> {
            log.error("Request ${call.prettyPrint()} failed", res.cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorBody(res))
        }
    }
}

private fun ApplicationCall.prettyPrint() = with(this.request) {
    "$httpMethod $uri"
}