package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class NavAnsattTilgangTilOppfolgingPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilOppfolgingPolicy {

	override val name = "NavAnsattTilgangTilOppfolgingPolicy"

	companion object {
		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til AD gruppen ${AdGrupper.MODIA_OPPFOLGING}",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun evaluate(input: NavAnsattTilgangTilOppfolgingPolicy.Input): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input.navIdent)

		val harTilgang = adGruppper.any { it.name == AdGrupper.MODIA_OPPFOLGING }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
