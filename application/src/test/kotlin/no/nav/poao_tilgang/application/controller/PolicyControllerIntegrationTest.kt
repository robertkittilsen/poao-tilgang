package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import okhttp3.Response
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class PolicyControllerIntegrationTest : IntegrationTest() {

	val navIdent = "Z1235"
	val norskIdent = "6456532"
	val navAnsattId = UUID.randomUUID()

	val noAccessGroup = AdGruppe(UUID.randomUUID(), "0000-some-group")

	@Autowired
	private lateinit var adGruppeProvider: AdGruppeProvider

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1 policy - permit`() {
		setupMocks()

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
		setupMocks()
		mockAbacHttpServer.mockDeny()

		val requestId = UUID.randomUUID()

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent": "$navIdent", "norskIdent": "$norskIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1"
		)

		response.body?.string() shouldBe denyResponse(requestId, "Deny fra ABAC", "IKKE_TILGANG_FRA_ABAC")
	}

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_MODIA_V1 policy - permit`() {
		val requestId = UUID.randomUUID()

		mockAdGrupperResponse(navIdent, navAnsattId, listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell))

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent":"$navIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_MODIA_V1"
		)

		response.body?.string() shouldBe permitResponse(requestId)
	}

	@Test
	fun `should evaluate NAV_ANSATT_TILGANG_TIL_MODIA_V1 policy - deny`() {
		val requestId = UUID.randomUUID()

		mockAdGrupperResponse(navIdent, navAnsattId, listOf(noAccessGroup))

		val response = sendPolicyRequest(
			requestId,
			"""{"navIdent":"$navIdent"}""",
			"NAV_ANSATT_TILGANG_TIL_MODIA_V1"
		)

		response.body?.string() shouldBe denyResponse(
			requestId,
			"NAV ansatt mangler tilgang til en av AD gruppene [0000-GA-BD06_ModiaGenerellTilgang, 0000-GA-Modia-Oppfolging, 0000-GA-SYFO-SENSITIV]",
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

	private fun mockAdGrupperResponse(navIdent: String, navAnsattId: UUID, adGrupper: List<AdGruppe>) {

		mockMicrosoftGraphHttpServer.mockHentAzureIdForNavAnsattResponse(navIdent, navAnsattId)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })


		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
	}

	private fun setupMocks() {
		mockPdlHttpServer.mockBrukerInfo(
			norskIdent = norskIdent,
			gtKommune = "1234"
		)

		mockSkjermetPersonHttpServer.mockErSkjermet(
			mapOf(
				norskIdent to false
			)
		)

		mockVeilarbarenaHttpServer.mockOppfolgingsenhet(norskIdent, "1234")
		mockAdGrupperResponse(navIdent, navAnsattId, listOf(noAccessGroup))

		mockAxsysHttpServer.mockHentTilgangerResponse(navIdent, listOf())
	}


}
