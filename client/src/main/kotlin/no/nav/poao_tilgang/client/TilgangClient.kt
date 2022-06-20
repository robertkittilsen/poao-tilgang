package no.nav.poao_tilgang.client

interface TilgangClient {
	fun harVeilederTilgangTilModia(navIdent: String): Decision
}
