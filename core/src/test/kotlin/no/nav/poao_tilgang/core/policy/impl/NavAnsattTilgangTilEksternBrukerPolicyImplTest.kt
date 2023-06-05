package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.domain.TilgangType.LESE
import no.nav.poao_tilgang.core.domain.TilgangType.SKRIVE
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.ToggleProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilEksternBrukerPolicyImplTest {

	private val navIdent = "Z1234"
	private val navAnsattAzureId = UUID.randomUUID()
	private val norskIdent = "63546454"

	private val abacProvider = mockk<AbacProvider>()
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy =
		mockk<NavAnsattTilgangTilAdressebeskyttetBrukerPolicy>()
	private val navAnsattTilgangTilSkjermetPersonPolicy = mockk<NavAnsattTilgangTilSkjermetPersonPolicy>()
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy = mockk<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy>()
	private val navAnsattTilgangTilOppfolgingPolicy = mockk<NavAnsattTilgangTilOppfolgingPolicy>()
	private val navAnsattTilgangTilModiaGenerellPolicy = mockk<NavAnsattTilgangTilModiaGenerellPolicy>()
	private val adGruppeProvider = mockk<AdGruppeProvider>()
	private val toggleProvider = mockk<ToggleProvider>()

	private val meterRegistry: MeterRegistry = SimpleMeterRegistry()

	private val policy = NavAnsattTilgangTilEksternBrukerPolicyImpl(
		abacProvider,
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy,
		navAnsattTilgangTilSkjermetPersonPolicy,
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy,
		navAnsattTilgangTilOppfolgingPolicy,
		navAnsattTilgangTilModiaGenerellPolicy,
		adGruppeProvider,
		meterRegistry,
		toggleProvider

	)

	@BeforeEach
	internal fun setupMocks() {
		every { toggleProvider.brukAbacDesision() } returns true
	}



	@Test
	fun `should return "permit" if ABAC returns "permit"`() {
		mockAbacDecision(true, LESE)
		mockDecision()

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if ABAC returns "deny"`() {
		mockAbacDecision(false, LESE)
		mockDecision()

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		decision shouldBe Decision.Deny("Deny fra ABAC", DecisionDenyReason.IKKE_TILGANG_FRA_ABAC)
	}

	@Test
	internal fun `should return PERMIT if abacs decision PERMIT and poao-tilgang is DENY`() {
		mockAbacDecision(true, LESE)
		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny()
		)

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `should return DENY if abacs decision DENY and poao-tilgang is PERMIT`() {
		mockAbacDecision(false, LESE)
		mockDecision()

		val decision = policy.evaluate(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		decision shouldBe Decision.Deny(
			message = "Deny fra ABAC",
			reason = DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

	@Test
	internal fun `harTilgang should return PERMIT for LESE`() {
		mockDecision()

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		verifyAll {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(any())
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(any())
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(any())
			navAnsattTilgangTilModiaGenerellPolicy.evaluate(any())
		}

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `harTilgang should return PERMIT for SKRIVE`() {
		mockDecision()

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		verifyAll {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(any())
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(any())
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(any())
			navAnsattTilgangTilOppfolgingPolicy.evaluate(any())
		}

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `harTilgang should return DENY if adressebeskyttet is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if skjermetPerson is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			skjermetPersonPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if eksternBrukerNavEnhet is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny(message),
			skjermetPersonPolicyDecision = deny(message),
			eksternBrukerNavEnhetPolicyDecision = deny(message),
			oppfolgingPolicyDecision = deny(message),
			modiaGenerellPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if tilgangTilOppfolging is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			oppfolgingPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}


	@Test
	internal fun `harTilgang should return DENY if tilgangType is SKRIVE and tilgangTilOppfolging is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			oppfolgingPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if tilgangType is LESE and tilgangTilmodiaGenerell is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			modiaGenerellPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		decision shouldBe deny(message)
	}

	private fun deny(message: String = "TEST", reason: DecisionDenyReason = DecisionDenyReason.POLICY_IKKE_IMPLEMENTERT): Decision.Deny {
		return Decision.Deny(
			message = message,
			reason = reason
		)
	}

	private fun mockAbacDecision(harTilgang: Boolean, tilgangType: TilgangType) {
		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns navIdent

		every {
			abacProvider.harVeilederTilgangTilPerson(navIdent, tilgangType, norskIdent)
		} returns harTilgang
	}

	private fun mockDecision(
		adressebeskyttetBrukerPolicyDecision: Decision = Decision.Permit,
		skjermetPersonPolicyDecision: Decision = Decision.Permit,
		eksternBrukerNavEnhetPolicyDecision: Decision = Decision.Permit,
		oppfolgingPolicyDecision: Decision = Decision.Permit,
		modiaGenerellPolicyDecision: Decision = Decision.Permit
	) {

		every {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
				NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
					navAnsattAzureId, norskIdent
				)
			)
		} returns adressebeskyttetBrukerPolicyDecision

		every {
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
				NavAnsattTilgangTilSkjermetPersonPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns skjermetPersonPolicyDecision

		every {
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns eksternBrukerNavEnhetPolicyDecision

		every {
			navAnsattTilgangTilOppfolgingPolicy.evaluate(
				NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
			)
		} returns oppfolgingPolicyDecision

		every {
			navAnsattTilgangTilModiaGenerellPolicy.evaluate(
				NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
			)
		} returns modiaGenerellPolicyDecision
	}
}
