package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

interface NavAnsattTilgangTilNavEnhetMedSperrePolicy : Policy<NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId,
		val navEnhetId: NavEnhetId
	) : PolicyInput

}
