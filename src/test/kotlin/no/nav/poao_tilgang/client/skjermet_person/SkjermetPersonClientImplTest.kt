package no.nav.poao_tilgang.client.skjermet_person

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class SkjermetPersonClientImplTest : FunSpec({

	val server = MockWebServer()
	val serverUrl = server.url("").toString().removeSuffix("/")

	afterSpec {
		server.shutdown()
	}

	test("erSkjermet - skal lage riktig request og parse respons") {
		val client = SkjermetPersonClientImpl(
			baseUrl = serverUrl,
			tokenProvider = { "TOKEN" },
		)

		val fnr1 = "123456789"
		val fnr2 = "573408953"

		server.enqueue(
			MockResponse().setBody(
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

		val request = server.takeRequest()

		request.path shouldBe "/skjermetBulk"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		val expectedRequestJson = """
			{"personidenter":["123456789","573408953"]}
		""".trimIndent()

		request.body.readUtf8() shouldBe expectedRequestJson
	}

})
