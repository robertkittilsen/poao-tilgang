package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.axsys.EnhetTilgang
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse

class MockAxsysHttpServer : MockHttpServer() {

	fun mockHentTilgangerResponse(navIdent: String, tilganger: List<EnhetTilgang>) {
		val response = MockResponse()
			.setBody(
				"""
					{
					  "enheter": [
						${
						tilganger.map {
							"""
									{
										"navn": "${it.enhetNavn}",
										"enhetId": "${it.enhetId}",
										"temaer": [${it.temaer.joinToString(",")}]
									}
								""".trimMargin()
						}.joinToString { "," }
					}
					  ]
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/api/v2/tilgang/$navIdent?inkluderAlleEnheter=false",
			matchMethod = "GET",
			response = response
		)
	}

}
