package no.nav.poao_tilgang.core.policy

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.impl.ModiaPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.Test
import java.util.*

class TilgangTilModiaPolicyTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val policy = ModiaPolicyImpl(adGruppeProvider)

	@Test
	fun `should return "permit" if access to 0000-ga-bd06_modiagenerelltilgang`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.MODIA_GENERELL),
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		policy.harTilgang(navIdent) shouldBe Decision.Permit
	}

	@Test
	fun `should return "permit" if access to 0000-ga-modia-oppfolging`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.MODIA_OPPFOLGING),
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		policy.harTilgang(navIdent) shouldBe Decision.Permit
	}

	@Test
	fun `should return "permit" if access to 0000-ga-syfo-sensitiv`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), AdGrupper.SYFO_SENSITIV),
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		policy.harTilgang(navIdent) shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if missing access to ad groups`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), "some-other-group")
		)

		val decision = policy.harTilgang(navIdent)

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-bd06_modiagenerelltilgang, 0000-ga-modia-oppfolging, 0000-ga-syfo-sensitiv]"
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}
