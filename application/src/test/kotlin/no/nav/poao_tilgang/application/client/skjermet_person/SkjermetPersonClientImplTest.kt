package no.nav.poao_tilgang.application.client.skjermet_person

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SkjermetPersonClientImplTest {

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
	fun `erSkjermet - skal lage riktig request og parse respons`() {
		val client = SkjermetPersonClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val fnr1 = "123456789"
		val fnr2 = "573408953"

		mockServer.handleRequest(
			response = MockResponse()
				.setBody(
					"""
						{
						  "$fnr1": true,
						  "$fnr2": false
						}
					""".trimIndent()
				)
		)

		val skjerming = client.erSkjermet(listOf(fnr1, fnr2))

		skjerming[fnr1] shouldBe true
		skjerming[fnr2] shouldBe false

		val request = mockServer.latestRequest()

		request.path shouldBe "/skjermetBulk"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		val expectedRequestJson = """
			{"personidenter":["123456789","573408953"]}
		""".trimIndent()

		request.body.readUtf8() shouldBe expectedRequestJson
	}

}
