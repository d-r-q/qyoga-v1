package qyoga

import qyoga.domain.DomainId

class NotFound(id: DomainId<*, *>) : Failure<Exception>(cause = Exception("Object $id not found"))

