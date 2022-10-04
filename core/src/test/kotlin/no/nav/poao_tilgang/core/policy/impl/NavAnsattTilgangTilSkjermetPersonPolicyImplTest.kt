package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleSkjermedePersonerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilSkjermetPersonPolicy
import no.nav.poao_tilgang.core.provider.SkjermetPersonProvider
import org.junit.jupiter.api.Test

class NavAnsattTilgangTilSkjermetPersonPolicyImplTest {

	private val skjermetPersonProvider = mockk<SkjermetPersonProvider>()

	private val navAnsattBehandleSkjermedePersonerPolicy = mockk<NavAnsattBehandleSkjermedePersonerPolicy>()

	private val policy = NavAnsattTilgangTilSkjermetPersonPolicyImpl(skjermetPersonProvider, navAnsattBehandleSkjermedePersonerPolicy)

	@Test
	fun `skal returnere "permit" hvis bruker ikke er skjermet`() {
		val norskIdent = "43543543"
		val navIdent = "Z1234"

		every {
			skjermetPersonProvider.erSkjermetPerson(norskIdent)
		} returns false

		val decision = policy.evaluate(NavAnsattTilgangTilSkjermetPersonPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "permit" hvis bruker er skjermet og NAV ansatt kan behandle skjermede personer`() {
		val norskIdent = "43543543"
		val navIdent = "Z1234"

		every {
			skjermetPersonProvider.erSkjermetPerson(norskIdent)
		} returns true

		every {
			navAnsattBehandleSkjermedePersonerPolicy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navIdent))
		} returns Decision.Permit

		val decision = policy.evaluate(NavAnsattTilgangTilSkjermetPersonPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis bruker er skjermet og NAV ansatt IKKE kan behandle skjermede personer`() {
		val norskIdent = "43543543"
		val navIdent = "Z1234"

		every {
			skjermetPersonProvider.erSkjermetPerson(norskIdent)
		} returns true

		every {
			navAnsattBehandleSkjermedePersonerPolicy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navIdent))
		} returns Decision.Deny("mangler ad gruppe", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)

		val decision = policy.evaluate(NavAnsattTilgangTilSkjermetPersonPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Deny("mangler ad gruppe", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)
	}

}
