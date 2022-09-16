package no.nav.poao_tilgang.api.dto.response

import java.util.*

data class PolicyEvaluationResultDto (
	val requestId: UUID,
	val decision: DecisionDto
)
