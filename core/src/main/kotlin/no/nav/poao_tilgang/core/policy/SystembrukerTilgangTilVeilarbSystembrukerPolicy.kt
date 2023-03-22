package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

interface SystembrukerTilgangTilVeilarbSystembrukerPolicy : Policy<SystembrukerTilgangTilVeilarbSystembrukerPolicy.Input> {
	data class Input(
		val systemressurs: String, // Systembrukernavn som ber om tilgang
	) : PolicyInput
}
