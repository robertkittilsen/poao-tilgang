package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy {

	companion object {
		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til AD gruppen ${AdGrupper.STRENGT_FORTROLIG_ADRESSE}",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override val name = "NavAnsattBehandleStrengtFortroligUtlandBrukere"

	override fun evaluate(input: NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy.Input): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input.navIdent)

		val harTilgang = adGruppper.any { it.name == AdGrupper.STRENGT_FORTROLIG_ADRESSE }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
