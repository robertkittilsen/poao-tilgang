package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetMedSperrePolicy
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import no.nav.poao_tilgang.core.utils.has

class NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val abacProvider: AbacProvider
) : NavAnsattTilgangTilNavEnhetMedSperrePolicy {

	private val aktivitetsplanKvp = adGruppeProvider.hentTilgjengeligeAdGrupper().aktivitetsplanKvp

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet med sperre",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhetMedSperre"

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		val harTilgangAbac = harTilgangAbac(input)

		asyncLogDecisionDiff(name, input, ::harTilgang, harTilgangAbac)

		return harTilgangAbac
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)
		val harTilgangAbac = abacProvider.harVeilederTilgangTilNavEnhetMedSperre(navIdent, input.navEnhetId)
		return toAbacDecision(harTilgangAbac)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.has(aktivitetsplanKvp)
			.whenPermit { return it }

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) Decision.Permit else denyDecision
	}

}
