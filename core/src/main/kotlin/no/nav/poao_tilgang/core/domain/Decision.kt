package no.nav.poao_tilgang.core.domain

sealed class Decision(val type: Type) {

	enum class Type {
		DENY,
		PERMIT
	}

	object Permit : Decision(Type.PERMIT)

	data class Deny(val message: String, val reason: DecisionDenyReason) : Decision(Type.DENY)

}
