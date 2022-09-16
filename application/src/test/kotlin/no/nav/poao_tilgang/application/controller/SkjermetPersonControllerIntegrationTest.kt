package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import org.junit.jupiter.api.Test

class SkjermetPersonControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `erSkjermet - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/skjermet-person",
			body = """{"norskeIdenter": ["1234354"]}""".toJsonRequestBody()
		)

		response.code shouldBe 401
	}

	@Test
	fun `erSkjermet - should return 403 when not machine-to-machine request`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/skjermet-person",
			body = """{"norskeIdenter": ["1234354"]}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdToken()}")
		)

		response.code shouldBe 403
	}

	@Test
	fun `erSkjermet - should return 200 with correct response`() {
		val norskIdent1 = "13487453"
		val norskIdent2 = "54364545"

		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(
			norskIdent1 to true,
			norskIdent2 to false
		))

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/skjermet-person",
			body = """{"norskeIdenter": ["$norskIdent1", "$norskIdent2"]}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}")
		)

		val expectedJson = """
			{"$norskIdent1":true,"$norskIdent2":false}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}

}
