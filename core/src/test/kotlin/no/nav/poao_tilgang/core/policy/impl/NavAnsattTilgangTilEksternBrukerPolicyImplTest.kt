package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilEksternBrukerPolicyImplTest {

	private val navIdent = "Z1234"
	private val norskIdent = "63546454"

	private val abacProvider = mockk<AbacProvider>()
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy =
		mockk<NavAnsattTilgangTilAdressebeskyttetBrukerPolicy>()
	private val navAnsattTilgangTilSkjermetPersonPolicy = mockk<NavAnsattTilgangTilSkjermetPersonPolicy>()
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy = mockk<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy>()
	private val navAnsattTilgangTilOppfolgingPolicy = mockk<NavAnsattTilgangTilOppfolgingPolicy>()

	private val policy = NavAnsattTilgangTilEksternBrukerPolicyImpl(
		abacProvider,
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy,
		navAnsattTilgangTilSkjermetPersonPolicy,
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy,
		navAnsattTilgangTilOppfolgingPolicy
	)

	@Test
	fun `should return "permit" if ABAC returns "permit"`() {
		mockAbacDecision(true)

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if ABAC returns "deny"`() {
		mockAbacDecision(false)

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Deny("Deny fra ABAC", DecisionDenyReason.IKKE_TILGANG_FRA_ABAC)
	}

	@Test
	internal fun `should return PERMIT if abacs decision PERMIT and poao-tilgang is DENY`() {
		mockAbacDecision(true)
		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny()
		)

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `should return DENY if abacs decision DENY and poao-tilgang is PERMIT`() {
		mockAbacDecision(false)
		mockDecision()

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe Decision.Deny(
			message = "Deny fra ABAC",
			reason = DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

	@Test
	internal fun `harTilgang should return DENY if adressebeskyttet is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny(message)
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if skjermetPerson is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			skjermetPersonPolicyDecision = deny(message)
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if eksternBrukerNavEnhet is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			eksternBrukerNavEnhetPolicyDecision = deny(message)
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if tilgangTilOppfolging is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			oppfolgingPolicyDecision = deny(message)
		)

		val decision = policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navIdent, norskIdent))

		decision shouldBe deny(message)
	}

	private fun deny(message: String = "TEST"): Decision.Deny {
		return Decision.Deny(
			message = message,
			reason = DecisionDenyReason.POLICY_IKKE_IMPLEMENTERT
		)
	}

	private fun mockAbacDecision(harTilgang: Boolean) {
		every {
			abacProvider.harVeilederTilgangTilPerson(navIdent, norskIdent)
		} returns harTilgang
	}

	private fun mockDecision(
		adressebeskyttetBrukerPolicyDecision: Decision = Decision.Permit,
		skjermetPersonPolicyDecision: Decision = Decision.Permit,
		eksternBrukerNavEnhetPolicyDecision: Decision = Decision.Permit,
		oppfolgingPolicyDecision: Decision = Decision.Permit
	) {

		every {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
				NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
					navIdent, norskIdent
				)
			)
		} returns adressebeskyttetBrukerPolicyDecision

		every {
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
				NavAnsattTilgangTilSkjermetPersonPolicy.Input(
					navIdent = navIdent,
					norskIdent = norskIdent
				)
			)
		} returns skjermetPersonPolicyDecision

		every {
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
					navIdent = navIdent,
					norskIdent = norskIdent
				)
			)
		} returns eksternBrukerNavEnhetPolicyDecision

		every {
			navAnsattTilgangTilOppfolgingPolicy.evaluate(
				NavAnsattTilgangTilOppfolgingPolicy.Input(navIdent)
			)
		} returns oppfolgingPolicyDecision

	}
}
