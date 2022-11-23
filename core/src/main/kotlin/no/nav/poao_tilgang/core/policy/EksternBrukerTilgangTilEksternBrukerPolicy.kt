package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en ekstern bruker har tilgang til en ekstern bruker (som oftest er dette samme person)
 */
interface EksternBrukerTilgangTilEksternBrukerPolicy : Policy<EksternBrukerTilgangTilEksternBrukerPolicy.Input> {

	data class Input(
		val rekvirentNorskIdent: NorskIdent, // Den som ber om tilgang
		val ressursNorskIdent: NorskIdent // Den som bes tilgang om
	) : PolicyInput
}
