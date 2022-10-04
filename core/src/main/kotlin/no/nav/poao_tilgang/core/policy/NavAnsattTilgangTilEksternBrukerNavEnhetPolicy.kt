package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om NAV ansatt har tilgang til brukers oppfølgingsenhet eller brukers geografiske enhet
 *
 */

interface NavAnsattTilgangTilEksternBrukerNavEnhetPolicy: Policy<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input> {

	data class Input(
		val navIdent: NavIdent,
		val norskIdent: NorskIdent
	) : PolicyInput

}


