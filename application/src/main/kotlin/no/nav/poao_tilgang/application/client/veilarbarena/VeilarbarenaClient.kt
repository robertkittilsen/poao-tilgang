package no.nav.poao_tilgang.application.client.veilarbarena

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent

interface VeilarbarenaClient {

	fun hentBrukerOppfolgingsenhetId(norskIdent: NorskIdent): NavEnhetId?

}
