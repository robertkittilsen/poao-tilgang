package no.nav.poao_tilgang.test_util.mock_clients

import no.nav.poao_tilgang.client.microsoft_graph.MicrosoftGraphClientImpl
import no.nav.poao_tilgang.domain.AdGruppe
import no.nav.poao_tilgang.test_util.MockHttpClient
import no.nav.poao_tilgang.utils.JsonUtils.toJsonString
import java.util.*

class MockMicrosoftGraphHttpClient : MockHttpClient() {

	fun enqueueHentAdGrupperResponse(grupper: List<AdGruppe>) {
		enqueue(
			body = toJsonString(
				MicrosoftGraphClientImpl.HentAdGrupperResponse(
					grupper.map { MicrosoftGraphClientImpl.HentAdGrupperResponse.AdGruppe(it.id, it.name) }
				)
			)
		)
	}

	fun enqueueHentAzureAdIdResponse(azureId: UUID) {
		enqueue(
			body = toJsonString(
				MicrosoftGraphClientImpl.HentAzureIdResponse(azureId)
			)
		)
	}

}
