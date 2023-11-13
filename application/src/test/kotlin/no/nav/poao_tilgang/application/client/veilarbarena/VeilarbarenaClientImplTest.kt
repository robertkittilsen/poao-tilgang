package no.nav.poao_tilgang.application.client.veilarbarena

import io.kotest.matchers.shouldBe
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.test_util.mock_clients.MockVeilarbarenaHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils
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

		val personRequest = PersonRequest(Fnr.of("987654"))
		val personRequestJSON = JsonUtils.toJsonString(personRequest)

		mockServer.mockOppfolgingsenhet("1234")

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId(personRequest)

		oppfolgingsenhetId shouldBe "1234"

		val request = mockServer.latestRequest()

		request.path shouldBe "/api/v2/arena/hent-status"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
		request.getHeader("Nav-Consumer-Id") shouldBe "poao-tilgang"
		request.body.readUtf8() shouldBe personRequestJSON
	}

	@Test
	fun `hentBrukerOppfolgingsenhetId skal returnere null hvis veilarbarena returnerer 404`() {
		val client = VeilarbarenaClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
			consumerId = "poao-tilgang"
		)

		val personRequest = PersonRequest(Fnr.of("987654"))
		mockServer.mockIngenOppfolgingsenhet(personRequest)

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId(personRequest)

		oppfolgingsenhetId shouldBe null
	}

}
