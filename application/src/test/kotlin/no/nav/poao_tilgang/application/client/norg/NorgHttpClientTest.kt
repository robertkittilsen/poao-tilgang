package no.nav.poao_tilgang.application.client.norg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.mock_clients.MockNorgHttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class NorgHttpClientTest {

	companion object {
		private val mockServer = MockNorgHttpServer()

		@BeforeAll
		@JvmStatic
		fun start() {
			mockServer.start()
		}
	}

	@AfterEach
	fun reset() {
		mockServer.reset()
	}

	@Test
	fun `hentTilhorendeEnhet skal lage riktig request og parse response`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345")

		tilhorendeEnhet shouldBe "4321"

		val request = mockServer.latestRequest()

		request.path shouldBe "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet feiler hvis Norg returnerer 404`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockIngenTilhorendeEnhet("23456")

		shouldThrow<RuntimeException> { client.hentTilhorendeEnhet("23456") }
	}
}
