package no.nav.poao_tilgang.application.test_util

import no.nav.poao_tilgang.application.Application
import no.nav.poao_tilgang.application.test_util.mock_clients.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration

@ActiveProfiles("test")
@Import(TestConfig::class)
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
open class IntegrationTest {

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient.Builder()
		.readTimeout(Duration.ofMinutes(15)) // For setting debug breakpoints without the connection being killed
		.build()

	companion object {
		val mockOAuthServer = MockOAuthServer()
		val mockMicrosoftGraphHttpServer = MockMicrosoftGraphHttpServer()
		val mockSkjermetPersonHttpServer = MockSkjermetPersonHttpServer()
		val mockAxsysHttpServer = MockAxsysHttpServer()
		val mockAbacHttpServer = MockAbacHttpServer()
		val mockVeilarbarenaHttpServer = MockVeilarbarenaHttpServer()
		val mockPdlHttpServer = MockPdlHttpServer()
		val mockNorgHttpServer = MockNorgHttpServer()

		@JvmStatic
		@DynamicPropertySource
		fun registerProperties(registry: DynamicPropertyRegistry) {
			mockOAuthServer.start()
			mockMicrosoftGraphHttpServer.start()
			mockSkjermetPersonHttpServer.start()
			mockAxsysHttpServer.start()
			mockAbacHttpServer.start()
			mockVeilarbarenaHttpServer.start()
			mockPdlHttpServer.start()
			mockNorgHttpServer.start()

			registry.add("no.nav.security.jwt.issuer.azuread.discovery-url", mockOAuthServer::getDiscoveryUrl)
			registry.add("no.nav.security.jwt.issuer.azuread.accepted-audience") { "test" }

			registry.add("microsoft_graph.url", mockMicrosoftGraphHttpServer::serverUrl)
			registry.add("skjermet_person.url", mockSkjermetPersonHttpServer::serverUrl)
			registry.add("axsys.url", mockAxsysHttpServer::serverUrl)
			registry.add("abac.url", mockAbacHttpServer::serverUrl)
			registry.add("veilarbarena.url", mockVeilarbarenaHttpServer::serverUrl)
			registry.add("pdl.url", mockPdlHttpServer::serverUrl)
			registry.add("norg.url", mockNorgHttpServer::serverUrl)

			setAdGrupperIder(registry)
		}

		private fun setAdGrupperIder(registry: DynamicPropertyRegistry) {
			registry.add("ad-gruppe-id.fortrolig-adresse") { "97690ad9-d423-4c1f-9885-b01fb9f9feab" }
			registry.add("ad-gruppe-id.strengt-fortrolig-adresse") {"49dfad60-e125-4216-b627-632f93054610"}
			registry.add("ad-gruppe-id.modia-admin") {"d765c025-d56c-4b15-b824-a8e12d9de60e"}
			registry.add("ad-gruppe-id.modia-oppfolging") {"d58e5b23-b7ea-4151-b6c1-8945c5438554"}
			registry.add("ad-gruppe-id.modia-generell") {"78d24b90-988a-4c6e-9862-3e0933ac2cd7"}
			registry.add("ad-gruppe-id.gosys-nasjonal") {"2866c090-cd46-4167-8e9e-4522d44312d0"}
			registry.add("ad-gruppe-id.gosys-utvidbar-til-nasjonal") {"b57870b5-3580-4e59-99f2-4c8c6083415d"}
			registry.add("ad-gruppe-id.gosys-utvidet") {"4ccf584e-098e-4625-aed7-97d82b450bcc"}
			registry.add("ad-gruppe-id.syfo-sensitiv") {"6681d1b1-e39f-4e34-b688-63584710772f"}
			registry.add("ad-gruppe-id.pensjon-utvidet") {"f7b20d6c-cf4b-47e0-b6ff-5383d9b6e57d"}
		}

	}

	@AfterEach
	fun reset() {
		mockMicrosoftGraphHttpServer.reset()
		mockSkjermetPersonHttpServer.reset()
		mockAxsysHttpServer.reset()
		mockAbacHttpServer.reset()
		mockVeilarbarenaHttpServer.reset()
		mockPdlHttpServer.reset()
		mockNorgHttpServer.reset()
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
