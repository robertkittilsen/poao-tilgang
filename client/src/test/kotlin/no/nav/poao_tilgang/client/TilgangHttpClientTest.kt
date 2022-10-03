package no.nav.poao_tilgang.client

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.beInstanceOf
import no.nav.common.rest.client.RestClient
import no.nav.poao_tilgang.application.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.test_util.TestConfig
import no.nav.poao_tilgang.client.api.BadHttpStatusApiException
import no.nav.poao_tilgang.client.api.NetworkApiException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.UnknownHostException
import java.time.Duration
import java.util.*

class TilgangHttpClientTest : IntegrationTest() {

	private val navIdent = "Z1234"

	private val fnr1 = "124253321"

	private val fnr2 = "654756834"

	private val navAnsattId = UUID.randomUUID()

	lateinit var client: PoaoTilgangHttpClient

	@BeforeEach
	fun setup() {
		client = PoaoTilgangHttpClient(
			serverUrl(),
			{ mockOAuthServer.issueAzureAdM2MToken() },
			RestClient.baseClientBuilder().readTimeout(Duration.ofMinutes(15)).build()
		)
	}

	@Test
	fun `evaluatePolicy - should evaluate EksternBrukerPolicy`() {
		mockAbacHttpServer.mockPermit()

		val decision = client.evaluatePolicy(NavAnsattTilgangTilEksternBrukerPolicyInput(navIdent, "34543543")).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate NavAnsattTilgangTilModiaPolicy`() {
		mockAdGrupperResponse(navIdent, navAnsattId, listOf("0000-ga-bd06_modiagenerelltilgang"))

		val decision = client.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(navIdent)).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `hentAdGrupper - skal hente ad grupper`() {
		mockAdGrupperResponse(navIdent, navAnsattId, listOf("0000-ga-123", "0000-ga-456"))

		val adGrupper = client.hentAdGrupper(navAnsattId).getOrThrow()

		adGrupper shouldHaveSize 2
		adGrupper.any { it.navn == "0000-ga-123" } shouldBe true
		adGrupper.any { it.navn == "0000-ga-456" } shouldBe true
	}

	@Test
	fun `erSkjermetPerson - skal hente enkelt skjermet person`() {
		mockAdGrupperResponse(navIdent, navAnsattId, listOf("0000-ga-123", "0000-ga-456"))

		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(
			fnr1 to true,
			fnr2 to false
		))

		client.erSkjermetPerson(fnr1).getOrThrow() shouldBe true
		client.erSkjermetPerson(fnr2).getOrThrow() shouldBe false
	}

	@Test
	fun `erSkjermetPerson - skal hente bulk skjermet person`() {
		mockAdGrupperResponse(navIdent, navAnsattId, listOf("0000-ga-123", "0000-ga-456"))

		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(
			fnr1 to true,
			fnr2 to false
		))

		val erSkjermet = client.erSkjermetPerson(listOf(fnr1, fnr2)).getOrThrow()

		erSkjermet[fnr1] shouldBe true
		erSkjermet[fnr2] shouldBe false
	}

	@Test
	fun `skal returnere BadHttpStatusApiException for feilende status`() {
		val badClient = PoaoTilgangHttpClient(serverUrl(), {""})

		val exception = badClient.erSkjermetPerson("34242").exception
		exception should beInstanceOf<BadHttpStatusApiException>()
		(exception as BadHttpStatusApiException).httpStatus shouldBe 401
		exception.responseBody shouldNotBe null
	}

	@Test
	fun `skal returnere NetworkApiException for netverk feil`() {
		val badClient = PoaoTilgangHttpClient("http://not-a-real-host", {""})

		val exception = badClient.erSkjermetPerson("34242").exception

		exception should beInstanceOf<NetworkApiException>()
		exception?.cause should beInstanceOf<UnknownHostException>()
	}

	private fun mockAdGrupperResponse(navIdent: String, navAnsattId: UUID, adGrupperNavn: List<String>) {
		val adGrupper = adGrupperNavn.map { AdGruppe(UUID.randomUUID(), it) }

		mockMicrosoftGraphHttpServer.mockHentAzureIdForNavAnsattResponse(navIdent, navAnsattId)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })

		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
	}
}
