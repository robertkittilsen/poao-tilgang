package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en NAV ansatt har tilgang til å bruke Modia flaten
 */
interface NavAnsattTilgangTilModiaPolicy : Policy<NavAnsattTilgangTilModiaPolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId
	) : PolicyInput

}
