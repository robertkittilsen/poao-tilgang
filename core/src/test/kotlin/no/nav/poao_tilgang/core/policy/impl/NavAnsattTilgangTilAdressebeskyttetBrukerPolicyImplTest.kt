package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilAdressebeskyttetBrukerPolicy
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import org.junit.jupiter.api.Test
import java.util.UUID

class NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImplTest {

	private val diskresjonskodeProvider = mockk<DiskresjonskodeProvider>()

	private val navAnsattBehandleFortroligBrukerePolicy = mockk<NavAnsattBehandleFortroligBrukerePolicy>()

	private val navAnsattBehandleStrengtFortroligBrukerePolicy = mockk<NavAnsattBehandleStrengtFortroligBrukerePolicy>()

	private val navAnsattBehandleStrengtFortroligUtlandBrukerePolicy = mockk<NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy>()

	private val policy = NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl(
		diskresjonskodeProvider,
		navAnsattBehandleFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligUtlandBrukerePolicy
	)

	private val navAnsattAzureId = UUID.randomUUID()

	@Test
	fun `skal returnere "permit" hvis bruker ikke har adressebeskyttelse`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns null

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "permit" hvis bruker er fortrolig og nav ansatt kan behandle fortrolig bruker`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns Diskresjonskode.FORTROLIG

		every {
			navAnsattBehandleFortroligBrukerePolicy.evaluate(
				NavAnsattBehandleFortroligBrukerePolicy.Input(navAnsattAzureId)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis bruker er fortrolig og nav ansatt IKKE kan behandle fortrolig bruker`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns Diskresjonskode.FORTROLIG

		every {
			navAnsattBehandleFortroligBrukerePolicy.evaluate(
				NavAnsattBehandleFortroligBrukerePolicy.Input(navAnsattAzureId)
			)
		} returns Decision.Deny("", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision.type shouldBe Decision.Type.DENY
	}

	@Test
	fun `skal returnere "permit" hvis bruker er strengt fortrolig og nav ansatt kan behandle strengt fortrolig bruker`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns Diskresjonskode.STRENGT_FORTROLIG

		every {
			navAnsattBehandleStrengtFortroligBrukerePolicy.evaluate(
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(navAnsattAzureId)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis bruker er strengt fortrolig og nav ansatt IKKE kan behandle strengt fortrolig bruker`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns Diskresjonskode.STRENGT_FORTROLIG

		every {
			navAnsattBehandleStrengtFortroligBrukerePolicy.evaluate(
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(navAnsattAzureId)
			)
		} returns Decision.Deny("", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision.type shouldBe Decision.Type.DENY
	}

	@Test
	fun `skal returnere "permit" hvis bruker er strengt fortrolig utland og nav ansatt kan behandle strengt fortrolig utland bruker`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns Diskresjonskode.STRENGT_FORTROLIG_UTLAND

		every {
			navAnsattBehandleStrengtFortroligUtlandBrukerePolicy.evaluate(
				NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy.Input(navAnsattAzureId)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis bruker er strengt fortrolig utland og nav ansatt IKKE kan behandle strengt fortrolig utland bruker`() {
		val norskIdent = "1235645644"

		every {
			diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
		} returns Diskresjonskode.STRENGT_FORTROLIG_UTLAND

		every {
			navAnsattBehandleStrengtFortroligUtlandBrukerePolicy.evaluate(
				NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy.Input(navAnsattAzureId)
			)
		} returns Decision.Deny("", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)

		val decision = policy.evaluate(NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision.type shouldBe Decision.Type.DENY
	}


}
