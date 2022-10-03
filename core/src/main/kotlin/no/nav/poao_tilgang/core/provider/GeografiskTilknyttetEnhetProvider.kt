package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent

interface GeografiskTilknyttetEnhetProvider {

	fun hentGeografiskTilknytetEnhet(norskIdent: NorskIdent): NavEnhetId?
}
