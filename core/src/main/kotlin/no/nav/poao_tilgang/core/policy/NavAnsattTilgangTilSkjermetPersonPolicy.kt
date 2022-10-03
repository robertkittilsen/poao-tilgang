package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

interface NavAnsattTilgangTilSkjermetPersonPolicy : Policy<NavAnsattTilgangTilSkjermetPersonPolicy.Input> {

	data class Input (
		val navIdent: NavIdent,
		val norskIdent: NorskIdent
	) : PolicyInput

}
