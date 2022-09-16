package no.nav.poao_tilgang.api.dto.request

import java.util.*

data class PolicyEvaluationRequestDto<I>(
    val requestId: UUID,
    val policyInput: I,
    val policyId: PolicyId
)
