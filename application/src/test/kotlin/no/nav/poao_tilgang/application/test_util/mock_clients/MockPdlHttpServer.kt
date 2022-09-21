package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse

class MockPdlHttpServer : MockHttpServer() {

	fun mockBrukerInfo(norskIdent: NorskIdent) {
		val response = MockResponse()
			.setBody(
				"""
					{
						"errors": null,
						"data": {
							"hentGeografiskTilknytning": {
								"gtType": "KOMMUNE",
								"gtKommune": "0570",
								"gtBydel": "OSLO",
								"gtLand": "NORGE"
							},
							"hentPerson": {
								"adressebeskyttelse": [
									{
										"gradering": "FORTROLIG"
									}
								]
							}
						}
					}
				""".trimIndent()
			)

		handleRequest(
			path = "/graphql",
			method = "POST",
			response = response
		)
	}

}
