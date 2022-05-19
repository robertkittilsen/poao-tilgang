package no.nav.poao_tilgang.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.domain.AdGruppe
import no.nav.poao_tilgang.test_util.ControllerIntegrationTest
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattAdGroupControllerIntegrationTest : ControllerIntegrationTest() {

	@Test
	fun `hentAdGrupperForNavAnsatt - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "GET",
			path = "/api/v1/ad-gruppe?navIdent=Z1234",
		)

		response.code shouldBe 401
	}

	@Test
	fun `hentAdGrupperForNavAnsatt - should return 200 when authenticated`() {
		mockMicrosoftGraphHttpClient.enqueueHentAzureAdIdResponse(
			UUID.randomUUID()
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperResponse(
			listOf(AdGruppe(id = UUID.fromString("a0036e11-5658-4d2d-aa6b-7056bdb4e758"), name = "TODO"))
		)

		val response = sendRequest(
			method = "GET",
			path = "/api/v1/ad-gruppe?navIdent=Z1234",
			body = null,
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdToken()}")
		)

		val expectedJson = """
			[{"id":"a0036e11-5658-4d2d-aa6b-7056bdb4e758","name":"TODO"}]
		""".trimIndent()

		response.body?.string() shouldBe expectedJson
		response.code shouldBe 200
	}

}
