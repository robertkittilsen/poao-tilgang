package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle skjermede personer (også kalt 'egen ansatt').
 */
interface NavAnsattBehandleSkjermedePersonerPolicy : Policy<NavAnsattBehandleSkjermedePersonerPolicy.Input> {

	data class Input (
		val navIdent: NavIdent
	) : PolicyInput

}
