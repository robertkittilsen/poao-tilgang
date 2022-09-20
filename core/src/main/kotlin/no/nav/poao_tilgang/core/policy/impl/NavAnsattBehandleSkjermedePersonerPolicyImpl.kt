package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleSkjermedePersonerPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class NavAnsattBehandleSkjermedePersonerPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleSkjermedePersonerPolicy {

	override val name = "NavAnsattBehandleSkjermedePersoner"

	companion object {
		private val behandleSkjermedePersonerGrupper = listOf(
			AdGrupper.GOSYS_UTVIDET,
			AdGrupper.PENSJON_UTVIDET
		).map { it.lowercase() }

		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til en av AD gruppene $behandleSkjermedePersonerGrupper",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun evaluate(input: NavAnsattBehandleSkjermedePersonerPolicy.Input): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input.navIdent)

		val harTilgang = adGruppper.any { behandleSkjermedePersonerGrupper.contains(it.name.lowercase()) }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
