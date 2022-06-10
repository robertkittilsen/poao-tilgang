package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class TilgangTilModiaPolicy(
	private val adGruppeProvider: AdGruppeProvider
) : Policy(PolicyType.TILGANG_TIL_MODIA) {

	companion object {
		private val tilgangTilModiaGrupper = listOf(
			AdGrupper.MODIA_GENERELL,
			AdGrupper.MODIA_OPPFOLGING,
			AdGrupper.SYFO_SENSITIV
		)

		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til en av AD gruppene $tilgangTilModiaGrupper",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	fun harTilgangTilModia(navIdent: String): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(navIdent)

		val harTilgang = adGruppper.any { tilgangTilModiaGrupper.contains(it.name) }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
