package no.nav.poao_tilgang.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.test_util.ControllerIntegrationTest
import org.junit.jupiter.api.Test

class NavAnsattAdGroupControllerIntegrationTest : ControllerIntegrationTest() {

	@Test
	fun `hentAdGrupperForNavAnsatt - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "GET",
			path = "/api/v1/ad-group?navIdent=Z1234",
		)

		response.code shouldBe 401
	}

	@Test
	fun `hentAdGrupperForNavAnsatt - should return 200 when authenticated`() {
		val response = sendRequest(
			method = "GET",
			path = "/api/v1/ad-group?navIdent=Z1234",
			body = null,
			headers = mapOf("Authorization" to "Bearer ${azureAdToken()}")
		)

		val expectedJson = """
			[]
		""".trimIndent()

		response.body?.string() shouldBe expectedJson
		response.code shouldBe 200
	}

}
