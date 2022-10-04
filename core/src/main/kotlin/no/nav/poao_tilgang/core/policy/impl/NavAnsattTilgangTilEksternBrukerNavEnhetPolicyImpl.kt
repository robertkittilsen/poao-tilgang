package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider

class NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
	private val oppfolgingsenhetProvider: OppfolgingsenhetProvider,
	private val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider,
	private val tilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicy
) : NavAnsattTilgangTilEksternBrukerNavEnhetPolicy {

	override val name = "NavAnsattTilgangTilEksternBrukerNavEnhetPolicy"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input): Decision {
		val (navIdent, norskIdent) = input

		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)?.let { navEnhetId ->
			return tilgangTilNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navIdent = navIdent,
					navEnhetId = navEnhetId
				)
			)
		}

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)?.let { navEnhetId ->
			return tilgangTilNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navIdent = navIdent,
					navEnhetId = navEnhetId
				)
			)
		}

		return Decision.Deny(
			message = "Brukeren har ikke oppfølgingsenhet eller geografisk enhet",
			reason = DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
		)

	}
}
