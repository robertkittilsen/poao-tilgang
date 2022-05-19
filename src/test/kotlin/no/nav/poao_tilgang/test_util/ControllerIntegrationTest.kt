package no.nav.poao_tilgang.test_util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@Import(TestConfig::class)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerIntegrationTest {

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient()

	companion object {
		val oAuthServer = MockOAuthServer()

		@AfterAll
		fun shutdown() {
			oAuthServer.shutdownMockServer()
		}
	}

	init {
		System.setProperty("MICROSOFT_GRAPH_URL", "http://localhost")
		System.setProperty("SKJERMET_PERSON_URL", "http://localhost")
	}

	fun serverUrl() = "http://localhost:$port"

	fun azureAdToken(
		subject: String = "test",
		audience: String = "test",
		claims: Map<String, Any> = emptyMap()
	): String {
		return oAuthServer.azureAdToken(subject, audience, claims)
	}

	fun sendRequest(
		method: String,
		path: String,
		body: RequestBody? = null,
		headers: Map<String, String> = emptyMap()
	): Response {
		val reqBuilder = Request.Builder()
			.url("${serverUrl()}$path")
			.method(method, body)

		headers.forEach {
			reqBuilder.addHeader(it.key, it.value)
		}

		return client.newCall(reqBuilder.build()).execute()
	}

}
