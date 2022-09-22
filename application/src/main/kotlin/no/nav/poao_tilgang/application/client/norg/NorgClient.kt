package no.nav.poao_tilgang.application.client.norg

import no.nav.poao_tilgang.core.domain.NavEnhetId

interface NorgClient {
    /**
     * Henter enheten som tilhører et geografisk område
     * @param geografiskTilknytning Geografisk identifikator, kommune eller bydel, for NAV kontoret (f.eks NAV Frogner tilhører 030105)
     * @return NAV enhet som tilhører det geografiske området
     */
    fun hentTilhorendeEnhet(geografiskTilknytning: String): NavEnhetId
}
