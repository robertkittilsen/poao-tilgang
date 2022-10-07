package no.nav.poao_tilgang.core.utils

import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason

fun List<AdGruppe>.has(gruppe: AdGruppe): Decision {
	val harTilgang = this.any { it.id == gruppe.id }

	return if (harTilgang) Decision.Permit else Decision.Deny(
		message = "NAV ansatt mangler tilgang til AD gruppen \"${gruppe.navn}\"",
		reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
	)
}

fun List<AdGruppe>.hasAtLeastOne(grupper: List<AdGruppe>): Decision {
	val harTilgang = this.any { gruppe -> grupper.any { gruppe.id == it.id } }

	return if (harTilgang) Decision.Permit else Decision.Deny(
		message = "NAV ansatt mangler tilgang til en av AD gruppene ${grupper.map { it.navn }}",
		reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
	)
}
