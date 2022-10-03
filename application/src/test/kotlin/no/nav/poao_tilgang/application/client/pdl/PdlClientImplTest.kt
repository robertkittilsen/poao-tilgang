package no.nav.poao_tilgang.application.client.pdl

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PdlClientImplTest {

	companion object {
		private val mockServer = MockHttpServer()

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

	private val client = PdlClientImpl(mockServer.serverUrl(), { "TOKEN" })

	@Test
	fun `hentBrukerInfo - gyldig respons - skal lage riktig request og parse response`() {
		val brukerIdent = "43534534"

		mockServer.handleRequest(
			response = MockResponse()
				.setBody("""
					{
						"errors": null,
						"data": {
							"hentGeografiskTilknytning": {
								"gtType": "KOMMUNE",
								"gtKommune": "0570",
								"gtBydel": null
							},
							"hentPerson": {
								"adressebeskyttelse": [
									{
										"gradering": "FORTROLIG"
									}
								]
							}
						}
					}
				""".trimIndent())
		)

		val brukerInfo = client.hentBrukerInfo(brukerIdent)

		brukerInfo.adressebeskyttelse shouldBe Adressebeskyttelse.FORTROLIG
		brukerInfo.geografiskTilknytning shouldBe GeografiskTilknytning(
			gtType = "KOMMUNE",
			gtKommune = "0570",
			gtBydel = null
		)

		val request = mockServer.latestRequest()

		request.path shouldBe "/graphql"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
		request.getHeader("Tema") shouldBe "GEN"
	}
}
