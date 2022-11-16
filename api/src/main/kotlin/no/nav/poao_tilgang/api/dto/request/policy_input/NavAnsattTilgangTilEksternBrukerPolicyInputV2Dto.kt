package no.nav.poao_tilgang.api.dto.request.policy_input

import java.util.*

data class NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto(
	val navAnsattAzureId: UUID,
	val norskIdent: String
)
