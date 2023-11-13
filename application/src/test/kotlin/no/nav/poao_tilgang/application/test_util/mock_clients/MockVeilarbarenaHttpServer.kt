package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.veilarbarena.PersonRequest
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.mockwebserver.MockResponse

class MockVeilarbarenaHttpServer : MockHttpServer() {

	fun mockOppfolgingsenhet(oppfolgingsenhet: NavEnhetId) {
		val response = MockResponse()
			.setBody(
				"""
					{
						"formidlingsgruppe": "ARBS",
						"kvalifiseringsgruppe": "BFORM",
						"rettighetsgruppe": "DAGP",
						"iservFraDato": "2021-11-16T10:09:03",
						"oppfolgingsenhet": "$oppfolgingsenhet"
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/api/v2/arena/hent-status",
			matchMethod = "POST",
			response = response
		)
	}

	fun mockIngenOppfolgingsenhet(personRequest: PersonRequest) {
		val personRequestJSON = JsonUtils.toJsonString(personRequest)
		val response = MockResponse()
			.setResponseCode(404)

		handleRequest(
			matchPath = "/api/v2/arena/hent-status",
			matchMethod = "POST",
			matchBodyContains = personRequestJSON,
			response = response
		)
	}

}
