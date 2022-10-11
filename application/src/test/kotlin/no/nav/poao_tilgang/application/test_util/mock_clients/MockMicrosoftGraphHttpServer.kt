package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClientImpl
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NavIdent
import okhttp3.mockwebserver.MockResponse
import java.util.*

class MockMicrosoftGraphHttpServer : MockHttpServer() {

	fun mockHentAdGrupperResponse(grupper: List<AdGruppe>) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAdGrupper.Response(
						grupper.map { MicrosoftGraphClientImpl.HentAdGrupper.Response.AdGruppe(it.id, it.navn) }
					)
				)
			)

		handleRequest(
			matchPath = "/v1.0/directoryObjects/getByIds?\$select=id,displayName",
			matchMethod = "POST",
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
			matchPath = "/v1.0/users/${navAnsattAzureId}/getMemberGroups",
			matchMethod = "POST",
			response = response
		)
	}

	fun mockHentAzureIdMedNavIdentResponse(navIdent: NavIdent, navAnsattAzureId: AzureObjectId) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAzureIdMedNavIdent.Response(listOf(
						MicrosoftGraphClientImpl.HentAzureIdMedNavIdent.Response.UserData(navAnsattAzureId)
					))
				)
			)

		handleRequest(
			matchPath = "/v1.0/users?\$select=id&\$count=true&\$filter=onPremisesSamAccountName%20eq%20%27$navIdent%27",
			matchMethod = "GET",
			response = response
		)
	}
	fun mockHentNavIdentMedAzureIdResponse(navAnsattAzureId: AzureObjectId, navIdent: NavIdent) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentNavIdentMedAzureId.Response(listOf(
						MicrosoftGraphClientImpl.HentNavIdentMedAzureId.Response.UserData(navIdent)
					))
				)
			)

		handleRequest(
			matchPath = "/v1.0/users?\$select=onPremisesSamAccountName&\$filter=id%20eq%20%27$navAnsattAzureId%27",
			matchMethod = "GET",
			response = response
		)
	}

}
