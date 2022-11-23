package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.EksternBrukerTilgangTilEksternBrukerPolicy

class EksternBrukerTilgangTilEksternBrukerPolicyImpl : EksternBrukerTilgangTilEksternBrukerPolicy{
	override val name = "EksternBrukerTilgangTilEksternBruker"

	override fun evaluate(input: EksternBrukerTilgangTilEksternBrukerPolicy.Input): Decision {
		return if (input.rekvirentNorskIdent == input.ressursNorskIdent) {
			 Decision.Permit
		} else {
			 Decision.Deny(
				message = "Rekvirent har ikke samme ident som ressurs",
				reason = DecisionDenyReason.EKSTERN_BRUKER_HAR_IKKE_TILGANG
			)
		}
	}
}
