package no.nav.poao_tilgang.application.client

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TilgangHttpClientTest : IntegrationTest() {

	private val navIdent = "Z1234"

	lateinit var client: PoaoTilgangHttpClient

	@BeforeEach
	fun setup() {
		client = PoaoTilgangHttpClient(
			serverUrl(),
			{ mockOAuthServer.issueAzureAdM2MToken() }
		)
	}

	@Test
	fun `evaluatePolicy - should evaluate ModiaPolicy`() {
		mockAdGrupperResponse(navIdent, listOf("0000-ga-bd06_modiagenerelltilgang"))

		val decision = client.evaluatePolicy(ModiaPolicyInput(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate FortroligBrukerPolicy`() {
		mockAdGrupperResponse(navIdent, listOf("0000-GA-GOSYS_KODE7"))

		val decision = client.evaluatePolicy(FortroligBrukerPolicyInput(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate StrengtFortroligBrukerPolicy`() {
		mockAdGrupperResponse(navIdent, listOf("0000-GA-GOSYS_KODE6"))

		val decision = client.evaluatePolicy(StrengtFortroligBrukerPolicyInput(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate SkjermetPersonPolicy`() {
		mockAdGrupperResponse(navIdent, listOf("0000-ga-TODO"))

		val decision = client.evaluatePolicy(SkjermetPersonPolicyInput(navIdent))

		decision shouldBe Decision.Permit
	}

	private fun mockAdGrupperResponse(navIdent: String, adGrupperNavn: List<String>) {
		val adGrupper = adGrupperNavn.map { AdGruppe(UUID.randomUUID(), it) }

		val navAnsattId = UUID.randomUUID()

		mockMicrosoftGraphHttpServer.mockHentAzureIdForNavAnsattResponse(navIdent, navAnsattId)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })

		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
	}
}
