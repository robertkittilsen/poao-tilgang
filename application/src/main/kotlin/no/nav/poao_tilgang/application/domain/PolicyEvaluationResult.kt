package no.nav.poao_tilgang.application.domain

import no.nav.poao_tilgang.core.domain.Decision
import java.util.*

data class PolicyEvaluationResult(
    val requestId: UUID,
    val decision: Decision
)
