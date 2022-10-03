package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent

interface OppfolgingsenhetProvider {

	fun hentOppfolgingsenhet(norskIdent: NorskIdent): NavEnhetId?

}
