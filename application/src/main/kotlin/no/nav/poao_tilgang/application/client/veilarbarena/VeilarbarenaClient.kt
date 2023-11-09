package no.nav.poao_tilgang.application.client.veilarbarena

import no.nav.poao_tilgang.core.domain.NavEnhetId

interface VeilarbarenaClient {

	fun hentBrukerOppfolgingsenhetId(personRequest: PersonRequest): NavEnhetId?

}
