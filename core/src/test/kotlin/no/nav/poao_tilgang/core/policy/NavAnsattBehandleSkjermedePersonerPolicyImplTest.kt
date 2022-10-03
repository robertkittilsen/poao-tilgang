package no.nav.poao_tilgang.core.policy

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.impl.NavAnsattBehandleSkjermedePersonerPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattBehandleSkjermedePersonerPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val policy = NavAnsattBehandleSkjermedePersonerPolicyImpl(adGruppeProvider)

	@Test
	fun `should return "permit" if access to 0000-GA-GOSYS_UTVIDET`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.GOSYS_UTVIDET),
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "permit" if access to 0000-GA-Pensjon_UTVIDET`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.PENSJON_UTVIDET),
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to correct AD group`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navIdent))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-gosys_utvidet, 0000-ga-pensjon_utvidet]"
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}

