package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.SystembrukerTilgangTilVeilarbSystembrukerPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class SystembrukerTilgangTilVeilarbSystembrukerPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private lateinit var policy: SystembrukerTilgangTilVeilarbSystembrukerPolicy

	private val navAnsattAzureId = UUID.randomUUID()

	private val systembruker = "srvtest"

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.testAdGrupper

		every {
			adGruppeProvider.hentAzureIdMedNavIdent(systembruker)
		} returns navAnsattAzureId

		policy = SystembrukerTilgangTilVeilarbSystembrukerPolicyImpl(adGruppeProvider)
	}

	@Test
	fun `should return "permit" if access to 0000-GA-VEILARB_SYSTEMBRUKER`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			TestAdGrupper.testAdGrupper.veilarbSystembruker,
			TestAdGrupper.randomGruppe
		)

		val decision = policy.evaluate(SystembrukerTilgangTilVeilarbSystembrukerPolicy.Input(systembruker))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to 0000-GA-Strengt_Fortrolig_Adresse`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			TestAdGrupper.randomGruppe
		)

		val decision = policy.evaluate(SystembrukerTilgangTilVeilarbSystembrukerPolicy.Input(systembruker))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV-ansatt mangler tilgang til AD-gruppen \"0000-GA-veilarb-systembruker\""
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}
}
