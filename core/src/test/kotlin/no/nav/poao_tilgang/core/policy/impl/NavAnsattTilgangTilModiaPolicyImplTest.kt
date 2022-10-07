package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.randomGruppe
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilModiaPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private lateinit var policy: NavAnsattTilgangTilModiaPolicy

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.testAdGrupper

		policy = NavAnsattTilgangTilModiaPolicyImpl(adGruppeProvider)
	}

	@Test
	fun `should return "permit" if access to 0000-GA-BD06_ModiaGenerellTilgang`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			testAdGrupper.modiaGenerell,
			randomGruppe
		)

		policy.evaluate(NavAnsattTilgangTilModiaPolicy.Input(navIdent)) shouldBe Decision.Permit
	}

	@Test
	fun `should return "permit" if access to 0000-GA-Modia-Oppfolging`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			testAdGrupper.modiaOppfolging,
			randomGruppe,
		)

		policy.evaluate(NavAnsattTilgangTilModiaPolicy.Input(navIdent)) shouldBe Decision.Permit
	}

	@Test
	fun `should return "permit" if access to 0000-GA-SYFO-SENSITIV`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			testAdGrupper.syfoSensitiv,
			randomGruppe,
		)

		policy.evaluate(NavAnsattTilgangTilModiaPolicy.Input(navIdent)) shouldBe Decision.Permit
	}
	@Test
	fun `should return "deny" if missing access to ad groups`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattTilgangTilModiaPolicy.Input(navIdent))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV ansatt mangler tilgang til en av AD gruppene [0000-GA-BD06_ModiaGenerellTilgang, 0000-GA-Modia-Oppfolging, 0000-GA-SYFO-SENSITIV]"
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}
