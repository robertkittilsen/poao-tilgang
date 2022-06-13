package no.nav.poao_tilgang.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.test_util.IntegrationTest
import no.nav.poao_tilgang.utils.RestUtils.toJsonRequestBody
import org.junit.jupiter.api.Test
import java.util.*

class TilgangControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `harTilgangTilModia - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgang/modia",
			body = """{"navIdent": "Z1234"}""".toJsonRequestBody()
		)

		response.code shouldBe 401
	}

	@Test
	fun `harTilgangTilModia - should return 403 when not machine-to-machine request`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgang/modia",
			body = """{"navIdent": "Z1234"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdToken()}")
		)

		response.code shouldBe 403
	}

	@Test
	fun `harTilgangTilModia - should return 'deny' if not member of correct ad group`() {
		val navIdent = "Z12371"
		val navAnsattAzureId = UUID.randomUUID()

		val adGroupId1 = UUID.randomUUID()
		val adGroupId2 = UUID.randomUUID()

		mockMicrosoftGraphHttpClient.enqueueHentAzureIdForNavAnsattResponse(
			navAnsattAzureId
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperForNavAnsatt(
			listOf(adGroupId1, adGroupId2)
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperResponse(
			listOf(AdGruppe(adGroupId1, "Gruppe1"), AdGruppe(adGroupId2, "Gruppe2"))
		)

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgang/modia",
			body = """{"navIdent": "$navIdent"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdM2MToken()}")
		)

		val expectedJson = """
			{"decision":{"message":"NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-bd06_modiagenerelltilgang, 0000-ga-modia-oppfolging, 0000-ga-syfo-sensitiv]","reason":"MANGLER_TILGANG_TIL_AD_GRUPPE","type":"DENY"}}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of correct ad group`() {
		val navIdent = "Z12371"
		val navAnsattAzureId = UUID.randomUUID()

		val adGroupId1 = UUID.randomUUID()
		val adGroupId2 = UUID.randomUUID()

		mockMicrosoftGraphHttpClient.enqueueHentAzureIdForNavAnsattResponse(
			navAnsattAzureId
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperForNavAnsatt(
			listOf(adGroupId1, adGroupId2)
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperResponse(
			listOf(AdGruppe(adGroupId1, AdGrupper.MODIA_OPPFOLGING), AdGruppe(adGroupId2, "Gruppe2"))
		)

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgang/modia",
			body = """{"navIdent": "$navIdent"}""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdM2MToken()}")
		)

		val expectedJson = """
			{"decision":{"type":"PERMIT"}}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}

}
