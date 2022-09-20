package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import okhttp3.Response
import org.junit.jupiter.api.Test
import java.util.*

class PolicyControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1 policy - permit`() {
		val navIdent = "Z1235"
		val norskIdent = "6456532"
		val requestId = UUID.randomUUID()

		mockAbacHttpServer.mockPermit()

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent": "$navIdent", "norskIdent": "$norskIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1"
		)

		response.body?.string() shouldBe permitResponse(requestId)
	}

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1 policy - deny`() {
		val navIdent = "Z1235"
		val norskIdent = "6456532"
		val requestId = UUID.randomUUID()

		mockAbacHttpServer.mockDeny()

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent": "$navIdent", "norskIdent": "$norskIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1"
		)

		response.body?.string() shouldBe denyResponse(requestId,"Deny fra ABAC", "IKKE_TILGANG_FRA_ABAC")
	}

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_MODIA_V1 policy - permit`() {
		val navIdent = "Z1235"
		val navAnsattId = UUID.randomUUID()
		val requestId = UUID.randomUUID()

		mockAdGrupperResponse(navIdent, navAnsattId, listOf("0000-ga-bd06_modiagenerelltilgang"))

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent":"$navIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_MODIA_V1"
		)

		response.body?.string() shouldBe permitResponse(requestId)
	}

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_MODIA_V1 policy - deny`() {
		val navIdent = "Z1235"
		val navAnsattId = UUID.randomUUID()
		val requestId = UUID.randomUUID()

		mockAdGrupperResponse(navIdent, navAnsattId, listOf("0000-some-group"))

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent":"$navIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_MODIA_V1"
		)

		response.body?.string() shouldBe denyResponse(
			requestId,
			"NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-bd06_modiagenerelltilgang, 0000-ga-modia-oppfolging, 0000-ga-syfo-sensitiv]",
			"MANGLER_TILGANG_TIL_AD_GRUPPE"
		)
	}

	private fun permitResponse(requestId: UUID): String {
		return """
			{"results":[{"requestId":"$requestId","decision":{"type":"PERMIT","message":null,"reason":null}}]}
		""".trimIndent()
	}

	private fun denyResponse(
		requestId: UUID,
		message: String,
		reason: String
	): String {
		return """
			{"results":[{"requestId":"$requestId","decision":{"type":"DENY","message":"$message","reason":"$reason"}}]}
		""".trimIndent()
	}

	private fun sendPolicyRequest(
		requestId: UUID,
		policyInputJsonObj: String,
		policyId: String
	): Response {
		return sendRequest(
			path = "/api/v1/policy/evaluate",
			method = "POST",
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}"),
			body = """
				{"requests": [
					{
						"requestId": "$requestId",
						"policyInput": $policyInputJsonObj,
						"policyId": "$policyId"
					}
				]}
			""".trimIndent().toJsonRequestBody()
		)
	}

	private fun mockAdGrupperResponse(navIdent: String, navAnsattId: UUID, adGrupperNavn: List<String>) {
		val adGrupper = adGrupperNavn.map { AdGruppe(UUID.randomUUID(), it) }

		mockMicrosoftGraphHttpServer.mockHentAzureIdForNavAnsattResponse(navIdent, navAnsattId)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })

		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
	}

}
