package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

interface NavAnsattTilgangTilNavEnhetPolicy : Policy<NavAnsattTilgangTilNavEnhetPolicy.Input> {

	data class Input (
		val navIdent: NavIdent,
		val navEnhetId: NavEnhetId
	) : PolicyInput

}
