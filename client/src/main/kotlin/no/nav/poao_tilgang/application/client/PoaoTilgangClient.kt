package no.nav.poao_tilgang.application.client

import java.util.*

interface PoaoTilgangClient {

	fun evaluatePolicy(input: PolicyInput): Decision

	fun evaluatePolicies(requests: List<PolicyRequest>): List<PolicyResult>

}

data class PolicyRequest(
	val requestId: UUID,
	val policyInput: PolicyInput
)

data class PolicyResult(
	val requestId: UUID,
	val decision: Decision
)
