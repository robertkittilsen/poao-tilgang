package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

interface NavAnsattTilgangTilNavEnhetPolicy : Policy<NavAnsattTilgangTilNavEnhetPolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId,
		val navEnhetId: NavEnhetId
	) : PolicyInput

}
