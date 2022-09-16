package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.axsys.EnhetTilgang
import no.nav.poao_tilgang.application.test_util.MockHttpClient
import no.nav.poao_tilgang.application.utils.JsonUtils

class MockAxsysHttpClient : MockHttpClient() {

	fun enqueueHentTilgangerResponse(tilganger: List<EnhetTilgang>) {
		enqueue(
			body = JsonUtils.toJsonString("""
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
		)
	}

}
