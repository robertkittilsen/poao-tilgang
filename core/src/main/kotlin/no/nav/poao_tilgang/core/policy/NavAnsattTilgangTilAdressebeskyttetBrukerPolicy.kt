package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en NAV ansatt har tilgang til bruker med adressebeskyttelse (diskresjonskode som f.eks kode6/7/19)
 */
interface NavAnsattTilgangTilAdressebeskyttetBrukerPolicy : Policy<NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input> {

	data class Input (
		val navIdent: NavIdent,
		val norskIdent: NorskIdent
	) : PolicyInput

}
