package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.mockwebserver.MockResponse

class MockNorgHttpServer : MockHttpServer() {

	fun mockTilhorendeEnhet(geografiskTilknytning: String, tilhorendeEnhet: NavEnhetId) {
		val response = MockResponse()
			.setBody(
				"""
					{
						"enhetNr": "$tilhorendeEnhet"
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/api/v1/enhet/navkontor/$geografiskTilknytning",
			matchMethod = "GET",
			response = response
		)
	}

	fun mockIngenTilhorendeEnhet(geografiskTilknytning: String) {
		val response = MockResponse()
			.setResponseCode(404)

		handleRequest(
			matchPath = "/api/v1/enhet/navkontor/$geografiskTilknytning",
			matchMethod = "GET",
			response = response
		)
	}
}
