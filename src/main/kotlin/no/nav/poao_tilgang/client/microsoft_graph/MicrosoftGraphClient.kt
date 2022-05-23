package no.nav.poao_tilgang.client.microsoft_graph

import no.nav.poao_tilgang.domain.AzureObjectId

interface MicrosoftGraphClient {

	fun hentAdGrupper(adGruppeAzureIder: List<AzureObjectId>): List<AdGruppe>

	fun hentAdGrupperForNavAnsatt(navAnsattAzureId: AzureObjectId): List<AzureObjectId>

	fun hentAzureIdForNavAnsatt(navIdent: String): AzureObjectId

}

data class AdGruppe(
	val id: AzureObjectId,
	val name: String
)
