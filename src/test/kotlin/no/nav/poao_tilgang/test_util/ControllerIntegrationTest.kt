package no.nav.poao_tilgang.test_util

import no.nav.poao_tilgang.test_util.mock_clients.MockMicrosoftGraphHttpClient
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
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
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
		val mockMicrosoftGraphHttpClient = MockMicrosoftGraphHttpClient()
		val mockSkjermetPersonHttpClient = MockHttpClient()

		@AfterAll
		fun shutdown() {
			oAuthServer.shutdownMockServer()
			mockMicrosoftGraphHttpClient.shutdown()
			mockSkjermetPersonHttpClient.shutdown()
		}

		@JvmStatic
		@DynamicPropertySource
		fun registerProperties(registry: DynamicPropertyRegistry) {
			registry.add("no.nav.security.jwt.issuer.azuread.discovery-url", oAuthServer::getDiscoveryUrl)
			registry.add("no.nav.security.jwt.issuer.azuread.accepted-audience") { "test" }

			registry.add("microsoft_graph.url", mockMicrosoftGraphHttpClient::serverUrl)
			registry.add("skjermet_person.url", mockSkjermetPersonHttpClient::serverUrl)
		}
	}

	init {
		System.setProperty("MICROSOFT_GRAPH_URL", "http://localhost")
		System.setProperty("SKJERMET_PERSON_URL", "http://localhost")
	}

	fun serverUrl() = "http://localhost:$port"

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
