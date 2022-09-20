package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle informasjon om en ekstern bruker.
 */
interface NavAnsattTilgangTilEksternBrukerPolicy : Policy<NavAnsattTilgangTilEksternBrukerPolicy.Input> {

	data class Input(
		val navIdent: NavIdent,
		val norskIdent: NorskIdent
	) : PolicyInput

}
