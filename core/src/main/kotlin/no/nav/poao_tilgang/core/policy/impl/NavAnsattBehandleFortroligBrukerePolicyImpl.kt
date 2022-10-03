package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class NavAnsattBehandleFortroligBrukerePolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleFortroligBrukerePolicy {

	override val name = "NavAnsattBehandleFortroligBrukere"

	companion object {
		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til AD gruppen ${AdGrupper.FORTROLIG_ADRESSE}",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun evaluate(input: NavAnsattBehandleFortroligBrukerePolicy.Input): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input.navIdent)

		val harTilgang = adGruppper.any { it.name == AdGrupper.FORTROLIG_ADRESSE }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
