package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

/**
 * Sjekker om NAV ansatt har tilgang til brukers oppfølgingsenhet eller brukers geografiske enhet
 *
 */

interface NavAnsattTilgangTilEksternBrukerNavEnhetPolicy: Policy<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input> {

	data class Input(
		val navAnsattAzureId: AzureObjectId,
		val norskIdent: NorskIdent
	) : PolicyInput

}


