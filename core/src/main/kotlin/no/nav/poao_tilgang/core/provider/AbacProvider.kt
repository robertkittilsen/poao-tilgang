package no.nav.poao_tilgang.core.provider

interface AbacProvider {

	fun harVeilederTilgangTilPerson(veilederIdent: String, eksternBrukerId: String): Boolean

}
