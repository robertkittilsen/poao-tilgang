package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilNavEnhetPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val navEnhetTilgangProvider = mockk<NavEnhetTilgangProvider>()

	private val abacProvider = mockk<AbacProvider>()

	private lateinit var policy: NavAnsattTilgangTilNavEnhetPolicyImpl

	private val navAnsattAzureId = UUID.randomUUID()

	private val navIdent = "Z1234"

	private val navEnhetId = "1234"

	private val meterRegistry: MeterRegistry = SimpleMeterRegistry()

	private val toggleProvider = mockk<ToggleProvider>()

	@BeforeEach
	internal fun setUp() {
		every { toggleProvider.brukAbacDesision() } returns true

		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns testAdGrupper

		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns navIdent

		policy = NavAnsattTilgangTilNavEnhetPolicyImpl(navEnhetTilgangProvider, adGruppeProvider, abacProvider, meterRegistry, toggleProvider)
	}

	@Test
	fun `should return "permit" if ABAC returns "permit"`() {
		every {
			abacProvider.harVeilederTilgangTilNavEnhet(navIdent, navEnhetId)
		} returns true

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetPolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if ABAC returns "deny"`() {
		every {
			abacProvider.harVeilederTilgangTilNavEnhet(navIdent, navEnhetId)
		} returns false

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetPolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny("Deny fra ABAC", DecisionDenyReason.IKKE_TILGANG_FRA_ABAC)
	}

	@Test
	fun `skal returnere "permit" hvis NAV ansatt har rollen 0000-GA-Modia_Admin`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			testAdGrupper.modiaOppfolging,
			testAdGrupper.modiaAdmin
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetPolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "permit" hvis tilgang til enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(testAdGrupper.modiaOppfolging)

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns listOf(
			NavEnhetTilgang(navEnhetId, "test", emptyList())
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetPolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis har ikke modia oppfolging`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns emptyList()

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetPolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny(
			"NAV-ansatt mangler tilgang til AD-gruppen \"0000-GA-Modia-Oppfolging\"",
			DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	@Test
	fun `skal returnere "deny" hvis ikke tilgang til enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(testAdGrupper.modiaOppfolging)

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns emptyList()

		val decision = policy.harTilgang(NavAnsattTilgangTilNavEnhetPolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny(
			"Har ikke tilgang til NAV enhet",
			DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
		)
	}

}
