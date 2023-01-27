package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetMedSperrePolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgang
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilNavEnhetMedSperrePolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val navEnhetTilgangProvider = mockk<NavEnhetTilgangProvider>()

	private val abacProvider = mockk<AbacProvider>()

	private lateinit var policy: NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl

	private val navAnsattAzureId = UUID.randomUUID()

	private val navIdent = "Z1234"

	private val navEnhetId = "1234"

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns testAdGrupper

		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns navIdent

		policy = NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(navEnhetTilgangProvider, adGruppeProvider, abacProvider)
	}

	@Test
	fun `should return "permit" if ABAC returns "permit"`() {
		every {
			abacProvider.harVeilederTilgangTilNavEnhetMedSperre(navIdent, navEnhetId)
		} returns true

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if ABAC returns "deny"`() {
		every {
			abacProvider.harVeilederTilgangTilNavEnhetMedSperre(navIdent, navEnhetId)
		} returns false

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny("Deny fra ABAC", DecisionDenyReason.IKKE_TILGANG_FRA_ABAC)
	}

	@Test
	fun `skal returnere "permit" hvis NAV ansatt har rollen 0000-GA-aktivitesplan_kvp`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			testAdGrupper.aktivitetsplanKvp
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "permit" hvis tilgang til enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns listOf(
			NavEnhetTilgang(navEnhetId, "test", emptyList())
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis ikke tilgang til enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns emptyList()

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny(
			"Har ikke tilgang til NAV enhet med sperre",
			DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
		)
	}

}
