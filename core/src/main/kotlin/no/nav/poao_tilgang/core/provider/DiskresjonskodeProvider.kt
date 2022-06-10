package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.Diskresjonskode

interface DiskresjonskodeProvider {

	fun hentDiskresjonskode(norskIdent: String): Diskresjonskode?

}
