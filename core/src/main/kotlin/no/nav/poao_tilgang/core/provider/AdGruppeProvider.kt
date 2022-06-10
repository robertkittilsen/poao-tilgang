package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.AdGruppe

interface AdGruppeProvider {

	fun hentAdGrupper(navIdent: String): List<AdGruppe>

}
