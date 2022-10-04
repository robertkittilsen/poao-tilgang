package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import org.junit.jupiter.api.Test

class NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImplTest {

	private val navIdent = "Z1234"
	private val norskIdent = "63546454"

	private val navEnhet = "1234"

	private val oppfolgingsenhetProvider = mockk<OppfolgingsenhetProvider>()
	private val geografiskTilknyttetEnhetProvider = mockk<GeografiskTilknyttetEnhetProvider>()
	private val tilgangTilNavEnhetPolicy = mockk<NavAnsattTilgangTilNavEnhetPolicy>()

	private val policy = NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
		oppfolgingsenhetProvider, geografiskTilknyttetEnhetProvider, tilgangTilNavEnhetPolicy
	)

	@Test
	internal fun `skal sjekke tilgang til oppfølgingsenhet hvis finnes`() {

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns navEnhet

		every {
			tilgangTilNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navIdent = navIdent,
					navEnhetId = navEnhet
				)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navIdent = navIdent,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit

		verify(exactly = 0) {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(any())
		}
	}

	@Test
	internal fun `skal sjekke tilgang til geografisk enhet hvis oppfølgingsenheten ikke finnes`() {

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns null

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} returns navEnhet

		every {
			tilgangTilNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navIdent = navIdent,
					navEnhetId = navEnhet
				)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navIdent = navIdent,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `skal returnere DENY om oppfølgingsenhet og geografisk enhet er null`() {

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns null

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} returns null

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navIdent = navIdent,
				norskIdent = norskIdent
			)
		)

		verify(exactly = 0) {
			tilgangTilNavEnhetPolicy.evaluate(any())
		}

		decision shouldBe Decision.Deny(
			message = "Brukeren har ikke oppfølgingsenhet eller geografisk enhet",
			reason = DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
		)
	}


}
