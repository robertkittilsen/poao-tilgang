package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClientImpl
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.core.domain.AzureObjectId
import okhttp3.mockwebserver.MockResponse
import java.util.*

class MockMicrosoftGraphHttpServer : MockHttpServer() {

	fun mockHentAdGrupperResponse(grupper: List<AdGruppe>) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAdGrupper.Response(
						grupper.map { MicrosoftGraphClientImpl.HentAdGrupper.Response.AdGruppe(it.id, it.name) }
					)
				)
			)

		handleRequest(
			path = "/v1.0/directoryObjects/getByIds?\$select=id,displayName",
			method = "POST",
			response = response
		)
	}

	fun mockHentAdGrupperForNavAnsatt(navAnsattAzureId: UUID, gruppeIder: List<AzureObjectId>) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAdGrupperForNavAnsatt.Response(
						value = gruppeIder
					)
				)
			)

		handleRequest(
			path = "/v1.0/users/${navAnsattAzureId}/getMemberGroups",
			method = "POST",
			response = response
		)
	}

	fun mockHentAzureIdForNavAnsattResponse(navIdent: String, navAnsattAzureId: UUID) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAzureIdForNavAnsatt.Response(listOf(
						MicrosoftGraphClientImpl.HentAzureIdForNavAnsatt.Response.UserData(navAnsattAzureId)
					))
				)
			)

		handleRequest(
			path = "/v1.0/users?\$select=id&\$count=true&\$filter=onPremisesSamAccountName%20eq%20%27$navIdent%27",
			method = "GET",
			response = response
		)
	}

}
