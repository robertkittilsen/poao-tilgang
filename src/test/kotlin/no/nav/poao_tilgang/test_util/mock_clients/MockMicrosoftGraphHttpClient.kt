package no.nav.poao_tilgang.test_util.mock_clients

import no.nav.poao_tilgang.client.microsoft_graph.MicrosoftGraphClientImpl
import no.nav.poao_tilgang.domain.AdGruppe
import no.nav.poao_tilgang.domain.AzureObjectId
import no.nav.poao_tilgang.test_util.MockHttpClient
import no.nav.poao_tilgang.utils.JsonUtils.toJsonString
import java.util.*

class MockMicrosoftGraphHttpClient : MockHttpClient() {

	fun enqueueHentAdGrupperResponse(grupper: List<AdGruppe>) {
		enqueue(
			body = toJsonString(
				MicrosoftGraphClientImpl.HentAdGrupper.Response(
					grupper.map { MicrosoftGraphClientImpl.HentAdGrupper.Response.AdGruppe(it.id, it.name) }
				)
			)
		)
	}

	fun enqueueHentAdGrupperForNavAnsatt(gruppeIder: List<AzureObjectId>) {
		enqueue(
			body = toJsonString(
				MicrosoftGraphClientImpl.HentAdGrupperForNavAnsatt.Response(
					values = gruppeIder
				)
			)
		)
	}

	fun enqueueHentAzureIdForNavAnsattResponse(navAnsattAzureId: UUID) {
		enqueue(
			body = toJsonString(
				MicrosoftGraphClientImpl.HentAzureIdForNavAnsatt.Response(listOf(
					MicrosoftGraphClientImpl.HentAzureIdForNavAnsatt.Response.UserData(navAnsattAzureId)
				))
			)
		)
	}

}
