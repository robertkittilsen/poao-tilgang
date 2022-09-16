package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.StrengtFortroligBrukerPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class StrengtFortroligBrukerPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : StrengtFortroligBrukerPolicy {

	override val name = "HarNavAnsattTilgangTilStrengtFortroligBruker"

	companion object {
		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til AD gruppen ${AdGrupper.GOSYS_KODE_6}",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun evaluate(input: StrengtFortroligBrukerPolicy.Input): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input.navIdent)

		val harTilgang = adGruppper.any { it.name == AdGrupper.GOSYS_KODE_6 }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
