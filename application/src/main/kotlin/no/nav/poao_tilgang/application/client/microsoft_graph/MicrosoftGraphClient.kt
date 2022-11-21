package no.nav.poao_tilgang.application.client.microsoft_graph

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NavIdent


interface MicrosoftGraphClient {

	fun hentAdGrupper(adGruppeAzureIder: List<AzureObjectId>): List<AdGruppe>

	fun hentAdGrupperForNavAnsatt(navAnsattAzureId: AzureObjectId): List<AzureObjectId>

	fun hentAzureIdMedNavIdent(navIdent: String): AzureObjectId

	fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent
}

data class AdGruppe(
	val id: AzureObjectId,
	val name: String
)
