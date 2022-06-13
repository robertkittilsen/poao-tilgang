package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.Policy

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle strengt fortrolig brukere (kode 6)
 */
interface StrengtFortroligBrukerPolicy : Policy<NavIdent>
