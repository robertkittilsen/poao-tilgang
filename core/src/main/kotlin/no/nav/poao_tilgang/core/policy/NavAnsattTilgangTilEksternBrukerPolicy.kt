package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle informasjon om en ekstern bruker.
 */
interface NavAnsattTilgangTilEksternBrukerPolicy : Policy<NavAnsattTilgangTilEksternBrukerPolicy.Input> {

	data class Input(
		val navAnsattAzureId: AzureObjectId,
		val norskIdent: NorskIdent
	) : PolicyInput

}
