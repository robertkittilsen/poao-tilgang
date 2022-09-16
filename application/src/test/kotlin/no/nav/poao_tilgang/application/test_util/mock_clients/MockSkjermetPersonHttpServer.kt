package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import okhttp3.mockwebserver.MockResponse

class MockSkjermetPersonHttpServer : MockHttpServer() {

	fun mockErSkjermet(skjerming: Map<String, Boolean>) {
		val response = MockResponse()
			.setBody(toJsonString(skjerming))

		handleRequest(
			path = "/skjermetBulk",
			method = "POST",
			response = response
		)
	}

}
