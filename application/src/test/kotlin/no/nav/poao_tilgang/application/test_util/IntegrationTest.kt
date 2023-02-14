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
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.*

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
		val mockMachineToMachineHttpServer = MockMachineToMachineHttpServer()

		@Suppress("UNUSED_PARAMETER")
		@JvmStatic
		@DynamicPropertySource
		fun registerProperties(_registry: DynamicPropertyRegistry) {
			setupClients()
			setupAdGrupperIder()

			mockOAuthServer.start()
			System.setProperty("AZURE_APP_WELL_KNOWN_URL", mockOAuthServer.getDiscoveryUrl())
			System.setProperty("AZURE_APP_CLIENT_ID", "test")


			mockMachineToMachineHttpServer.start()
			System.setProperty("AZURE_APP_JWK", MockMachineToMachineHttpServer.jwk)
			System.setProperty(
				"AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
				mockMachineToMachineHttpServer.serverUrl() + MockMachineToMachineHttpServer.tokenPath
			)
		}


		private fun setupClients() {

			mockSkjermetPersonHttpServer.start()
			System.setProperty("SKJERMET_PERSON_URL", mockSkjermetPersonHttpServer.serverUrl())
			System.setProperty("SKJERMET_PERSON_SCOPE", "api://test.nom.skjermede-personer-pip/.default")

			mockMicrosoftGraphHttpServer.start()
			System.setProperty("MICROSOFT_GRAPH_URL", mockMicrosoftGraphHttpServer.serverUrl())
			System.setProperty("MICROSOFT_GRAPH_SCOPE", "https://graph.microsoft.com/.default")


			mockAxsysHttpServer.start()
			System.setProperty("AXSYS_URL", mockAxsysHttpServer.serverUrl())
			System.setProperty("AXSYS_SCOPE", "api://test.org.axsys/.default")


			mockAbacHttpServer.start()
			System.setProperty("ABAC_URL", mockAbacHttpServer.serverUrl())
			System.setProperty("ABAC_SCOPE", "api://test.pto.abac-veilarb-proxy/.default")

			mockVeilarbarenaHttpServer.start()
			System.setProperty("VEILARBARENA_URL", mockVeilarbarenaHttpServer.serverUrl())
			System.setProperty("VEILARBARENA_SCOPE", "api://test.pto.veilarbarena/.default")

			mockPdlHttpServer.start()
			System.setProperty("PDL_URL", mockPdlHttpServer.serverUrl())
			System.setProperty("PDL_SCOPE", "api://test.pdl.pdl-api/.default")

			mockNorgHttpServer.start()
			System.setProperty("NORG_URL", mockNorgHttpServer.serverUrl())
		}

		private fun setupAdGrupperIder() {
			System.setProperty("AD_GRUPPE_ID_FORTROLIG_ADRESSE", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_STRENGT_FORTROLIG_ADRESSE", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_MODIA_ADMIN", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_MODIA_OPPFOLGING", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_MODIA_GENERELL", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_GOSYS_NASJONAL", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_GOSYS_UTVIDBAR_TIL_NASJONAL", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_GOSYS_UTVIDET", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_SYFO_SENSITIV", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_PENSJON_UTVIDET", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_EGNE_ANSATTE", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_AKTIVITETSPLAN_KVP", UUID.randomUUID().toString())
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
