package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleSkjermedePersonerPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.randomGruppe
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattBehandleSkjermedePersonerPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private lateinit var policy: NavAnsattBehandleSkjermedePersonerPolicy

	private val navAnsattAzureId = UUID.randomUUID()

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.testAdGrupper

		policy = NavAnsattBehandleSkjermedePersonerPolicyImpl(adGruppeProvider)
	}

	@Test
	fun `should return "permit" if access to 0000-GA-Egne_ansatte`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			testAdGrupper.egneAnsatte,
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navAnsattAzureId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to correct AD group`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattBehandleSkjermedePersonerPolicy.Input(navAnsattAzureId))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV-ansatt mangler tilgang til en av AD-gruppene [0000-GA-Egne_ansatte]"
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}

