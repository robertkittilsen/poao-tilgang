package no.nav.poao_tilgang.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.test_util.IntegrationTest
import no.nav.poao_tilgang.utils.RestUtils.toJsonRequestBody
import org.junit.jupiter.api.Test

class SkjermetPersonControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `erSkjermet - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/skjermet-person",
			body = """{"norskIdent": "1234354"}""".toJsonRequestBody()
		)

		response.code shouldBe 401
	}

	@Test
	fun `erSkjermet - should return 403 when not machine-to-machine request`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/skjermet-person",
			body = """{"norskIdent": "3423453453"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdToken()}")
		)

		response.code shouldBe 403
	}


	@Test
	fun `erSkjermet - should return 200 with correct response`() {
		val norskIdent = "13487453"

		mockSkjermetPersonHttpClient.enqueueErSkjermet(mapOf(norskIdent to true))

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/skjermet-person",
			body = """{"norskIdent": "$norskIdent"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdM2MToken()}")
		)

		val expectedJson = """
			{"erSkjermet":true}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}

}
