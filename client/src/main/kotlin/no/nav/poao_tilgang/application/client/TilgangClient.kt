package no.nav.poao_tilgang.application.client

interface TilgangClient {
	fun harVeilederTilgangTilModia(navIdent: String): Decision
}
