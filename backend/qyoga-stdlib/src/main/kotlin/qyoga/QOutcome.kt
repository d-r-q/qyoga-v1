package qyoga

import qyoga.domain.DomainId

class NotFoundCause(override val message: String) : Exception(message)
class NotFound(id: DomainId<*, *>) : Failure<NotFoundCause>(cause = NotFoundCause("Object $id not found"))

