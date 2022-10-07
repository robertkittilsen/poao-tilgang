package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import okhttp3.Response
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class TilgangControllerIntegrationTest : IntegrationTest() {

	val noAccessGroup = AdGruppe(UUID.randomUUID(), "NO_ACCESS_GROUP")

	@Autowired
	private lateinit var adGruppeProvider: AdGruppeProvider

	@Test
	fun `harTilgangTilModia - should return 401 when not authenticated`() {
		val response = sendTilgangTilModiaRequest("Z1234")

		response.code shouldBe 401
	}

	@Test
	fun `harTilgangTilModia - should return 403 when not machine-to-machine request`() {
		val response = sendTilgangTilModiaRequest("Z1234") { mockOAuthServer.issueAzureAdToken() }

		response.code shouldBe 403
	}

	@Test
	fun `harTilgangTilModia - should return 'deny' if not member of any ad group`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(navIdent, emptyList())

		val response = sendTilgangTilModiaRequest(navIdent) { mockOAuthServer.issueAzureAdM2MToken() }

		expectDeny(
			response = response,
			message = "NAV ansatt mangler tilgang til en av AD gruppene [0000-GA-BD06_ModiaGenerellTilgang, 0000-GA-Modia-Oppfolging, 0000-GA-SYFO-SENSITIV]",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	@Test
	fun `harTilgangTilModia - should return 'deny' if not member of correct ad group`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(
			navIdent, listOf(
				noAccessGroup
			)
		)

		val response = sendTilgangTilModiaRequest(navIdent) { mockOAuthServer.issueAzureAdM2MToken() }

		expectDeny(
			response = response,
			message = "NAV ansatt mangler tilgang til en av AD gruppene [0000-GA-BD06_ModiaGenerellTilgang, 0000-GA-Modia-Oppfolging, 0000-GA-SYFO-SENSITIV]",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of modiagenerelltilgang`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(navIdent, listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell))

		val response = sendTilgangTilModiaRequest(navIdent) { mockOAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of modia-oppfolging`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(navIdent, listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging))

		val response = sendTilgangTilModiaRequest(navIdent) { mockOAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of syfo-sensitiv`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(navIdent, listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().syfoSensitiv))

		val response = sendTilgangTilModiaRequest(navIdent) { mockOAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of correct role and other`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(
			navIdent, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging,
				noAccessGroup
			)
		)

		val response = sendTilgangTilModiaRequest(navIdent) { mockOAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	private fun mockAdGrupperResponse(navIdent: String, adGrupper: List<AdGruppe>) {
		val navAnsattId = UUID.randomUUID()

		mockMicrosoftGraphHttpServer.mockHentAzureIdForNavAnsattResponse(navIdent, navAnsattId)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })

		if (adGrupper.isNotEmpty()) {
			mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
		}
	}

	private fun sendTilgangTilModiaRequest(navIdent: String, token: (() -> String)? = null): Response {
		val headers = if (token != null) mapOf("Authorization" to "Bearer ${token()}") else mapOf()
		return sendRequest(
			method = "POST",
			path = "/api/v1/tilgang/modia",
			body = """{"navIdent": "$navIdent"}""".toJsonRequestBody(),
			headers = headers
		)
	}

	private fun expectPermit(response: Response) {
		val expectedJson = """
			{"decision":{"type":"PERMIT","message":null,"reason":null}}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}

	private fun expectDeny(response: Response, message: String, reason: DecisionDenyReason) {
		val expectedJson = """
			{"decision":{"type":"DENY","message":"$message","reason":"$reason"}}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}
}
