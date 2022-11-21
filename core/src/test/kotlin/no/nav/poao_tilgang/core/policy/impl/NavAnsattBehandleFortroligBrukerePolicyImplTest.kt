package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.randomGruppe
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class NavAnsattBehandleFortroligBrukerePolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private lateinit var policy: NavAnsattBehandleFortroligBrukerePolicy

	private val navAnsattAzureId = UUID.randomUUID()

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.testAdGrupper

		policy = NavAnsattBehandleFortroligBrukerePolicyImpl(adGruppeProvider)
	}

	@Test
	fun `should return "permit" if access to 0000-GA-Fortrolig_Adresse`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			TestAdGrupper.testAdGrupper.fortroligAdresse,
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattBehandleFortroligBrukerePolicy.Input(navAnsattAzureId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to 0000-GA-Fortrolig_Adresse`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattBehandleFortroligBrukerePolicy.Input(navAnsattAzureId))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV-ansatt mangler tilgang til AD-gruppen \"0000-GA-Fortrolig_Adresse\""
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}

