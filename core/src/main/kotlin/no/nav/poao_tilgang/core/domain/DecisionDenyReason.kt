package no.nav.poao_tilgang.core.domain

enum class DecisionDenyReason {
	MANGLER_TILGANG_TIL_AD_GRUPPE,
	POLICY_NOT_IMPLEMENTED,
	IKKE_TILGANG_FRA_ABAC
}
