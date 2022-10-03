package no.nav.poao_tilgang.application.client.axsys

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AxsysClientImplTest {

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

	@Test
	fun `hentTilganger - med fodselsnummer - skal returnere korrekt parset respons`() {
		val brukerident = "AB12345"

		val client = AxsysClientImpl(
			baseUrl = mockServer.serverUrl(),
			proxyTokenProvider = { "PROXY_TOKEN" },
			axsysTokenProvider = { "AXSYS_TOKEN" },
		)

		System.setProperty("NAIS_APP_NAME", "poao-tilgang")

		mockServer.handleRequest(
			response = MockResponse()
				.setBody(
					"""
						{
						  "enheter": [
							{
							  "enhetId": "0104",
							  "temaer": [
								"MOB",
								"OPA",
								"HJE"
							  ],
							  "navn": "NAV Testheim"
							}
						  ]
						}
					""".trimIndent()
				)
		)

		val tilganger = client.hentTilganger(brukerident)
		val enhetTilgang = tilganger.first()

		enhetTilgang.enhetNavn shouldBe "NAV Testheim"
		enhetTilgang.enhetId shouldBe "0104"
		enhetTilgang.temaer.containsAll(listOf("MOB", "OPA", "HJE")) shouldBe true

		val request = mockServer.latestRequest()

		request.path shouldBe "/api/v2/tilgang/$brukerident?inkluderAlleEnheter=false"
		request.getHeader("Authorization") shouldBe "Bearer PROXY_TOKEN"
		request.getHeader("Downstream-Authorization") shouldBe "Bearer AXSYS_TOKEN"

		request.getHeader("Nav-Consumer-Id") shouldBe "poao-tilgang"
		request.getHeader("Nav-Call-Id") shouldMatch "[0-9a-fA-F]{30,32}"
	}

}
