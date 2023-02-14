package no.nav.poao_tilgang.api.dto.request.policy_input

import java.util.*

data class NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto(
	val navAnsattAzureId: UUID,
	val navEnhetId: String
)
