package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.policy.ModiaPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class ModiaPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : ModiaPolicy {

	companion object {
		private val tilgangTilModiaGrupper = listOf(
			AdGrupper.MODIA_GENERELL,
			AdGrupper.MODIA_OPPFOLGING,
			AdGrupper.SYFO_SENSITIV
		).map { it.lowercase() }

		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til en av AD gruppene $tilgangTilModiaGrupper",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun harTilgang(input: NavIdent): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input)

		val harTilgang = adGruppper.any { tilgangTilModiaGrupper.contains(it.name.lowercase()) }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
