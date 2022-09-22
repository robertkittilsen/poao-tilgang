package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse

class MockVeilarbarenaHttpServer : MockHttpServer() {

	fun mockOppfolgingsenhet(norskIdent: NorskIdent, oppfolgingsenhet: NavEnhetId) {
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
			matchPath = "/api/arena/status?fnr=$norskIdent",
			matchMethod = "GET",
			response = response
		)
	}

	fun mockIngenOppfolgingsenhet(norskIdent: NorskIdent) {
		val response = MockResponse()
			.setResponseCode(404)

		handleRequest(
			matchPath = "/api/arena/status?fnr=$norskIdent",
			matchMethod = "GET",
			response = response
		)
	}

}
