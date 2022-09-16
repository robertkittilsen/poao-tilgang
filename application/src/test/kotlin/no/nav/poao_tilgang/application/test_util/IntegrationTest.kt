package no.nav.poao_tilgang.application.test_util

import no.nav.poao_tilgang.application.test_util.mock_clients.MockAxsysHttpClient
import no.nav.poao_tilgang.application.test_util.mock_clients.MockMicrosoftGraphHttpClient
import no.nav.poao_tilgang.application.test_util.mock_clients.MockSkjermetPersonHttpClient
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@Import(TestConfig::class)
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class IntegrationTest {

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient()

	companion object {
		val oAuthServer = MockOAuthServer()
		val mockMicrosoftGraphHttpClient = MockMicrosoftGraphHttpClient()
		val mockSkjermetPersonHttpClient = MockSkjermetPersonHttpClient()
		val mockAxsysHttpClient = MockAxsysHttpClient()

		@JvmStatic
		@DynamicPropertySource
		fun registerProperties(registry: DynamicPropertyRegistry) {
			oAuthServer.start()
			mockMicrosoftGraphHttpClient.start()
			mockSkjermetPersonHttpClient.start()
			mockAxsysHttpClient.start()

			registry.add("no.nav.security.jwt.issuer.azuread.discovery-url", oAuthServer::getDiscoveryUrl)
			registry.add("no.nav.security.jwt.issuer.azuread.accepted-audience") { "test" }

			registry.add("microsoft_graph.url", mockMicrosoftGraphHttpClient::serverUrl)
			registry.add("skjermet_person.url", mockSkjermetPersonHttpClient::serverUrl)

			registry.add("axsys.url", mockAxsysHttpClient::serverUrl)

		}
	}

	@AfterEach
	fun resetRequestCount() {
		mockMicrosoftGraphHttpClient.resetRequestCount()
		mockSkjermetPersonHttpClient.resetRequestCount()
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
