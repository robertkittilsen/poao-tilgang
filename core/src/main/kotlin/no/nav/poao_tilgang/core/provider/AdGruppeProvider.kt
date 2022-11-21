package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NavIdent

interface AdGruppeProvider {

	fun hentAdGrupper(navAnsattAzureId: AzureObjectId): List<AdGruppe>

	fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent

	fun hentAzureIdMedNavIdent(navIdent: NavIdent): AzureObjectId

	fun hentTilgjengeligeAdGrupper(): AdGrupper

}
