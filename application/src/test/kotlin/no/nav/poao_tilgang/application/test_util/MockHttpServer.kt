package no.nav.poao_tilgang.application.test_util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.slf4j.LoggerFactory

open class MockHttpServer {

	private val server = MockWebServer()

	private val log = LoggerFactory.getLogger(javaClass)

	private var lastRequestCount = 0

	private val responseHandlers = mutableMapOf<(request: RecordedRequest) -> Boolean, MockResponse>()

	fun start() {
		try {
		    server.start()
			server.dispatcher = createResponseDispatcher()
		} catch (e: IllegalArgumentException) {
			log.info("${javaClass.simpleName} is already started")
		}
	}

	fun reset() {
		lastRequestCount = server.requestCount
		responseHandlers.clear()
	}

	fun serverUrl(): String {
		return server.url("").toString().removeSuffix("/")
	}

	fun addResponseHandler(requestMatcher: (req: RecordedRequest) -> Boolean, response: MockResponse) {
		responseHandlers[requestMatcher] = response
	}

	fun handleRequest(
		path: String? = null,
		method: String? = null,
		headers: Map<String, String>? = null,
		body: String? = null,
		response: MockResponse
	) {
		val requestMatcher = matcher@{ req: RecordedRequest ->
			if (path != null && req.path != path)
				return@matcher false

			if (method != null && req.method != method)
				return@matcher false

			if (headers != null && !hasExpectedHeaders(req.headers, headers))
				return@matcher false

			if (body != null && req.body.readUtf8() != body)
				return@matcher false

			true
		}

		addResponseHandler(requestMatcher, response)
	}

	fun latestRequest(): RecordedRequest {
		return server.takeRequest()
	}

	fun requestCount(): Int {
		return server.requestCount - lastRequestCount
	}

	fun shutdown() {
		server.shutdown()
	}

	private fun createResponseDispatcher(): Dispatcher {
		return object : Dispatcher() {
			override fun dispatch(request: RecordedRequest): MockResponse {
				val response = responseHandlers.entries.find { it.key.invoke(request) }?.value
					?: throw IllegalStateException("No handler for $request")

				log.info("Responding [${request.path}]: $response")

				return response
			}
		}
	}

	private fun hasExpectedHeaders(requestHeaders: okhttp3.Headers, expectedHeaders: Map<String, String>): Boolean {
		var hasHeaders = true

		expectedHeaders.forEach { (name, value) ->
			if (requestHeaders[name] != value)
				hasHeaders = false
		}

		return hasHeaders
	}

}
