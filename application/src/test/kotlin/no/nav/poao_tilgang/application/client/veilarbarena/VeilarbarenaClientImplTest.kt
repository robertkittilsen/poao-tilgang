package no.nav.poao_tilgang.application.client.veilarbarena

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.mock_clients.MockVeilarbarenaHttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class VeilarbarenaClientImplTest {

	companion object {
		private val mockServer = MockVeilarbarenaHttpServer()

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
	fun `hentBrukerOppfolgingsenhetId skal lage riktig request og parse respons`() {
		val client = VeilarbarenaClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
			consumerId = "poao-tilgang"
		)

		mockServer.mockOppfolgingsenhet("987654", "1234")

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId("987654")

		oppfolgingsenhetId shouldBe "1234"

		val request = mockServer.latestRequest()

		request.path shouldBe "/api/arena/status?fnr=987654"
		request.method shouldBe "GET"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
		request.getHeader("Nav-Consumer-Id") shouldBe "poao-tilgang"
	}

	@Test
	fun `hentBrukerOppfolgingsenhetId skal returnere null hvis veilarbarena returnerer 404`() {
		val client = VeilarbarenaClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
			consumerId = "poao-tilgang"
		)

		mockServer.mockIngenOppfolgingsenhet("987654")

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId("987654")

		oppfolgingsenhetId shouldBe null
	}

}
