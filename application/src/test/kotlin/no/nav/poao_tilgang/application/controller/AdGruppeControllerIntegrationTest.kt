package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import org.junit.jupiter.api.Test
import java.util.*

class AdGruppeControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `hentAlleAdGrupperForBruker - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/ad-gruppe",
			body = """{"navAnsattAzureId": "${UUID.randomUUID()}"}""".toJsonRequestBody()
		)

		response.code shouldBe 401
	}

	@Test
	fun `hentAlleAdGrupperForBruker - should return 403 when not machine-to-machine request`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/ad-gruppe",
			body = """{"navAnsattAzureId": "${UUID.randomUUID()}"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdToken()}")
		)

		response.code shouldBe 403
	}

	@Test
	fun `hentAlleAdGrupperForBruker - should return 200 with correct response`() {
		val adGruppe = AdGruppe(id = UUID.fromString("a0036e11-5658-4d2d-aa6b-7056bdb4e758"), name = "TODO")

		val navAnsattId = UUID.randomUUID()

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(
			navAnsattId, listOf(adGruppe.id)
		)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(
			listOf(adGruppe)
		)

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/ad-gruppe",
			body = """{"navAnsattAzureId": "$navAnsattId"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}")
		)

		val expectedJson = """
			[{"id":"${adGruppe.id}","name":"${adGruppe.name}"}]
		""".trimIndent()

		response.body?.string() shouldBe expectedJson
		response.code shouldBe 200
	}

}
