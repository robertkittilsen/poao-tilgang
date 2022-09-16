package no.nav.poao_tilgang.application.domain

import no.nav.poao_tilgang.core.domain.PolicyInput
import java.util.*

data class PolicyEvaluationRequest(
    val requestId: UUID,
    val input: PolicyInput
)
