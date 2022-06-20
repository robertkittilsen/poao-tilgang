package no.nav.poao_tilgang.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.test_util.IntegrationTest
import org.junit.jupiter.api.Test
import java.util.*

class TilgangHttpClientTest : IntegrationTest() {

	private val navIdent = "Z1234"

	@Test
	fun `harTilgangTilModia - should return 401 when not authenticated`() {
		val exception = shouldThrow<RuntimeException> {
			TilgangHttpClient(serverUrl(), { "" })
				.harVeilederTilgangTilModia(navIdent)
		}
		exception.message shouldBe ("Feilende kall med statuskode 401 mot ${serverUrl()}/api/v1/tilgang/modia")
	}

	@Test
	fun `harTilgangTilModia - should return 403 when not machine-to-machine request`() {
		val tilgangHttpClient = TilgangHttpClient(serverUrl(), { oAuthServer.issueAzureAdToken() })
		val exception = shouldThrow<RuntimeException> {
			tilgangHttpClient.harVeilederTilgangTilModia(navIdent)
		}
		exception.message shouldBe ("Feilende kall med statuskode 403 mot ${serverUrl()}/api/v1/tilgang/modia")
	}

	@Test
	fun `harTilgangTilModia - should return 'deny' if not member of correct ad group`() {

		mockAdGrupperResponse(listOf("Gruppe1", "Gruppe2"))

		val decision = TilgangHttpClient(serverUrl(), { oAuthServer.issueAzureAdM2MToken() })
			.harVeilederTilgangTilModia(navIdent)

		decision shouldBe Decision.Deny(
			"NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-bd06_modiagenerelltilgang, 0000-ga-modia-oppfolging, 0000-ga-syfo-sensitiv]",
			"MANGLER_TILGANG_TIL_AD_GRUPPE"
		)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of correct ad group`() {
		mockAdGrupperResponse(listOf(AdGrupper.MODIA_OPPFOLGING	, "Gruppe2"))

		val decision = TilgangHttpClient(serverUrl(), { oAuthServer.issueAzureAdM2MToken() })
			.harVeilederTilgangTilModia(navIdent)

		decision shouldBe Decision.Permit
	}

	private fun mockAdGrupperResponse(adGrupperNavn: List<String>) {
		val adGrupper = adGrupperNavn.map { AdGruppe(UUID.randomUUID(), it) }

		mockMicrosoftGraphHttpClient.enqueueHentAzureIdForNavAnsattResponse(UUID.randomUUID())

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperForNavAnsatt(adGrupper.map { it.id })

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperResponse(adGrupper)
	}
}
