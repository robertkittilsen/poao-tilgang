package no.nav.poao_tilgang.core.policy

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.impl.EksternBrukerPolicyImpl
import no.nav.poao_tilgang.core.provider.AbacProvider
import org.junit.jupiter.api.Test

class EksternBrukerPolicyImplTest {

	private val abacProvider = mockk<AbacProvider>()

	private val policy = EksternBrukerPolicyImpl(abacProvider)

	@Test
	fun `should return "permit" if ABAC returns "permit"`() {
		val navIdent = "Z1234"
		val norskIdent = "63546454"

		every {
			abacProvider.harVeilederTilgangTilPerson(navIdent, norskIdent)
		} returns true

		val decision = policy.evaluate(EksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if ABAC returns "deny"`() {
		val navIdent = "Z1234"
		val norskIdent = "63546454"

		every {
			abacProvider.harVeilederTilgangTilPerson(navIdent, norskIdent)
		} returns false

		val decision = policy.evaluate(EksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Deny("Deny fra ABAC", DecisionDenyReason.IKKE_TILGANG_FRA_ABAC)
	}

}
