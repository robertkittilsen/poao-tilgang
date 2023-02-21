package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPaNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import no.nav.poao_tilgang.core.utils.hasAtLeastOne

class NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
	private val oppfolgingsenhetProvider: OppfolgingsenhetProvider,
	private val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider,
	private val tilgangTilOppfolgingPaNavEnhetPolicy: NavAnsattTilgangTilOppfolgingPaNavEnhetPolicy,
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilEksternBrukerNavEnhetPolicy {

	private val nasjonalTilgangGrupper = adGruppeProvider.hentTilgjengeligeAdGrupper().let {
		listOf(
			it.gosysNasjonal,
			it.gosysUtvidbarTilNasjonal,
		)
	}

	override val name = "NavAnsattTilgangTilEksternBrukerNavEnhetPolicy"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input

		// Hvis man har nasjonal tilgang så trengs det ikke sjekk på enhet tilgang
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.hasAtLeastOne(nasjonalTilgangGrupper)
			.whenPermit { return it }

		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)?.let { navEnhetId ->
			return tilgangTilOppfolgingPaNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilOppfolgingPaNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					navEnhetId = navEnhetId
				)
			)
		}

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)?.let { navEnhetId ->
			return tilgangTilOppfolgingPaNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilOppfolgingPaNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
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
