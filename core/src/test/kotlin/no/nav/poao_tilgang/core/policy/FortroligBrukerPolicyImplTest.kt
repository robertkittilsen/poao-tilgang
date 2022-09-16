package no.nav.poao_tilgang.core.policy

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.impl.FortroligBrukerPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.Test
import java.util.*

class FortroligBrukerPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val policy = FortroligBrukerPolicyImpl(adGruppeProvider)

	@Test
	fun `should return "permit" if access to 0000-GA-GOSYS_KODE7`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.GOSYS_KODE_7),
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(FortroligBrukerPolicy.Input(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to 0000-GA-GOSYS_KODE7`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(FortroligBrukerPolicy.Input(navIdent))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV ansatt mangler tilgang til AD gruppen 0000-GA-GOSYS_KODE7"
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}

