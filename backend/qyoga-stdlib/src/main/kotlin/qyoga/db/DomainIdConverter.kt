package qyoga.db

import io.ebean.config.ScalarTypeConverter
import qyoga.domain.DomainId


open class DomainIdConverter<DID : DomainId<ID, E>, ID, out E>(private val wrapValueImpl: (ID) -> DID) :
    ScalarTypeConverter<DID, ID> {

    override fun getNullValue(): DID? {
        return null
    }

    override fun wrapValue(scalarType: ID): DID =
        this.wrapValueImpl(scalarType)

    override fun unwrapValue(beanType: DID): ID? =
        beanType.value

}