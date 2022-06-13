package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.policy.SkjermetPersonPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class SkjermetPersonPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : SkjermetPersonPolicy {

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
