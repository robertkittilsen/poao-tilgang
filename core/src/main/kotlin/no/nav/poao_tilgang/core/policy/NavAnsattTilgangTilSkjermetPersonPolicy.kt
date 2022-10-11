package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

interface NavAnsattTilgangTilSkjermetPersonPolicy : Policy<NavAnsattTilgangTilSkjermetPersonPolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId,
		val norskIdent: NorskIdent
	) : PolicyInput

}
