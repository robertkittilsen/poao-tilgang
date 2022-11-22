package no.nav.poao_tilgang.api.dto.request.policy_input

import no.nav.poao_tilgang.api.dto.request.TilgangType
import java.util.*

data class NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto(
	val navAnsattAzureId: UUID,
	val tilgangType: TilgangType,
	val norskIdent: String
)
