package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse

class MockPdlHttpServer : MockHttpServer() {

	fun mockBrukerInfo(
		norskIdent: NorskIdent,
		gradering: String? = null,
		gtType: String = "KOMMUNE",
		gtKommune: String? = null,
		gtBydel: String? = null
	) {
		val response = MockResponse()
			.setBody(
				"""
					{
						"errors": null,
						"data": {
							"hentGeografiskTilknytning": {
								"gtType": "$gtType",
								"gtKommune": ${gtKommune?.let { """"$it"""" }},
								"gtBydel": ${gtBydel?.let { """"$it"""" }}
							},
							"hentPerson": {
								"adressebeskyttelse": [
									{
										"gradering": ${gradering?.let { """"$it"""" }}
									}
								]
							}
						}
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/graphql",
			matchMethod = "POST",
			matchBodyContains = norskIdent,
			response = response
		)
	}

}
