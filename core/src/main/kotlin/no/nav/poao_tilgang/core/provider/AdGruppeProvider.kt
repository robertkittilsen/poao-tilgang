package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.AdGruppe
import java.util.*

interface AdGruppeProvider {

	fun hentAdGrupper(navIdent: String): List<AdGruppe>

	fun hentAdGrupper(azureId: UUID): List<AdGruppe>

}
