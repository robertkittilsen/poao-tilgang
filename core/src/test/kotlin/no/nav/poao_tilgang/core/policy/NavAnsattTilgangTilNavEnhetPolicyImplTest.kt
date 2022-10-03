package no.nav.poao_tilgang.core.policy

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilNavEnhetPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgang
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilNavEnhetPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val navEnhetTilgangProvider = mockk<NavEnhetTilgangProvider>()

	private val policy = NavAnsattTilgangTilNavEnhetPolicyImpl(navEnhetTilgangProvider, adGruppeProvider)

	@Test
	fun `skal returnere "permit" hvis NAV ansatt har rollen 0000-GA-Modia_Admin`() {
		val enhetId = "1234"
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.MODIA_ADMIN)
		)

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetPolicy.Input(navIdent, enhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "permit" hvis tilgang til enhet`() {
		val navIdent = "Z1234"
		val enhetId = "1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns emptyList()

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns listOf(
			NavEnhetTilgang(enhetId, "test", emptyList())
		)

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetPolicy.Input(navIdent, enhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis ikke tilgang til enhet`() {
		val navIdent = "Z1234"
		val enhetId = "1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns emptyList()

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns emptyList()

		val decision = policy.evaluate(NavAnsattTilgangTilNavEnhetPolicy.Input(navIdent, enhetId))

		decision shouldBe Decision.Deny(
			"Har ikke tilgang til NAV enhet",
			DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
		)
	}

}
