package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision

class NavAnsattTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
	private val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
	private val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilEksternBrukerPolicy {

	override val name = "NavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val harTilgangAbac = harTilgangAbac(input)

		asyncLogDecisionDiff(name, input, ::harTilgang, harTilgangAbac)

		return harTilgangAbac
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, tilgangType, norskIdent)

		return toAbacDecision(harTilgang)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input

		when (tilgangType) {
			TilgangType.LESE ->
				navAnsattTilgangTilModiaGenerellPolicy.evaluate(
					NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }

			TilgangType.SKRIVE ->
				navAnsattTilgangTilOppfolgingPolicy.evaluate(
					NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }
		}

		navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
			NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
			NavAnsattTilgangTilSkjermetPersonPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		return Decision.Permit
	}

}
