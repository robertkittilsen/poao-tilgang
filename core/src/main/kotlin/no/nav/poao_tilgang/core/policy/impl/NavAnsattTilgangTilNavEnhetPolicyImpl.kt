package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import no.nav.poao_tilgang.core.utils.has

class NavAnsattTilgangTilNavEnhetPolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val abacProvider: AbacProvider
) : NavAnsattTilgangTilNavEnhetPolicy {

	private val modiaAdmin = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaAdmin

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhet"

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val harTilgangAbac = harTilgangAbac(input)

		asyncLogDecisionDiff(name, input, ::harTilgang, harTilgangAbac)

		return harTilgangAbac
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)
		val harTilgangAbac = abacProvider.harVeilederTilgangTilNavEnhet(navIdent, input.navEnhetId)
		return toAbacDecision(harTilgangAbac)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.has(modiaAdmin)
			.whenPermit { return it }

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) Decision.Permit else denyDecision
	}

}
