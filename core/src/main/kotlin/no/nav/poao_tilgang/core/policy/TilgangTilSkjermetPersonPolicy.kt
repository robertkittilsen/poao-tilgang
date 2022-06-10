package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class TilgangTilSkjermetPersonPolicy(
	private val adGruppeProvider: AdGruppeProvider
) : Policy<NavIdent>(PolicyType.TILGANG_TIL_SKJERMET_PERSON) {

	companion object {
		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til AD gruppen ${AdGrupper.SKJERMET_PERSON}",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun harTilgang(input: NavIdent): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input)

		val harTilgang = adGruppper.any { it.name == AdGrupper.SKJERMET_PERSON }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
