package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.EksternBrukerTilgangTilEksternBrukerPolicy
import org.junit.jupiter.api.Test

class EksternBrukerTilgangTilEksternBrukerPolicyImplTest {

	private val policy = EksternBrukerTilgangTilEksternBrukerPolicyImpl()

	@Test
	fun `skal returnere "permit" hvis rekvirent og ressurs er lik`() {


		val decision = policy.evaluate(
			EksternBrukerTilgangTilEksternBrukerPolicy.Input(
				rekvirentNorskIdent = "34523456",
				ressursNorskIdent = "34523456"
			)
		)

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis rekvirent og ressurs er ulik`() {


		val decision = policy.evaluate(
			EksternBrukerTilgangTilEksternBrukerPolicy.Input(
				rekvirentNorskIdent = "34523456",
				ressursNorskIdent = "56756567"
			)
		)

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "Rekvirent har ikke samme ident som ressurs"
			decision.reason shouldBe DecisionDenyReason.EKSTERN_BRUKER_HAR_IKKE_TILGANG
		}
	}
}
