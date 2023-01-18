package no.nav.poao_tilgang.api.dto.request.policy_input

import java.util.*

data class NavAnsattTilgangTilNavEnhetPolicyInputV1Dto (
	val navAnsattAzureId: UUID,
	val navEnhetId: String
)
