package no.nav.poao_tilgang.test_util

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

open class MockHttpClient {

	private val server = MockWebServer()

	init {
	    server.start()
	}

	fun serverUrl(): String {
		return server.url("").toString().removeSuffix("/")
	}

	fun enqueue(response: MockResponse) {
		server.enqueue(response)
	}

	fun enqueue(
		responseCode: Int = 200,
		headers: Map<String, String> = emptyMap(),
		body: String
	) {
		val response = MockResponse()
			.setBody(body)
			.setResponseCode(responseCode)

		headers.forEach {
			response.addHeader(it.key, it.value)
		}

		server.enqueue(response)
	}

	fun latestRequest(): RecordedRequest {
		return server.takeRequest()
	}

	fun requestCount(): Int {
		return server.requestCount
	}

	fun shutdown() {
		server.shutdown()
	}

}
