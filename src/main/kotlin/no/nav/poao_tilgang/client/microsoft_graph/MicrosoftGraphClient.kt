package no.nav.poao_tilgang.client.microsoft_graph

import no.nav.poao_tilgang.domain.AzureObjectId
import java.util.*

interface MicrosoftGraphClient {

	fun hentAdGrupper(azureId: AzureObjectId): List<AdGruppe>

	fun hentAzureId(navIdent: String): AzureObjectId

}

data class AdGruppe(
	val id: UUID,
	val name: String
)
