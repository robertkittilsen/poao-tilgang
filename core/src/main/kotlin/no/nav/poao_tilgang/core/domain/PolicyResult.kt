package no.nav.poao_tilgang.core.domain

data class PolicyResult(
	val policyName: String,
	val decision: Decision,
)
