package no.nav.poao_tilgang.core.domain

sealed class Decision(val type: Type) {

	enum class Type {
		DENY,
		PERMIT
	}

	val isPermit: Boolean get() = type == Type.PERMIT

	val isDeny: Boolean get() = type == Type.DENY

	object Permit : Decision(Type.PERMIT)

	data class Deny(val message: String, val reason: DecisionDenyReason) : Decision(Type.DENY)

	inline fun whenPermit(action: (permit: Permit) -> Unit) {
		if (this is Permit) {
			action.invoke(this)
		}
	}

	inline fun whenDeny(action: (deny: Deny) -> Unit) {
		if (this is Deny) {
			action.invoke(this)
		}
	}
}
