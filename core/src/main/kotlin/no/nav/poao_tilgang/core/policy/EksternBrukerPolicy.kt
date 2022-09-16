package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle informasjon om en ekstern bruker.
 */
interface EksternBrukerPolicy : Policy<EksternBrukerPolicy.Input> {

	data class Input(
		val navIdent: String,
		val norskIdent: String
	) : PolicyInput

}
