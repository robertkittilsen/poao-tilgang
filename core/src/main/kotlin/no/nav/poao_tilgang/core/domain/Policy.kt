package no.nav.poao_tilgang.core.domain

interface Policy<I> {

	// Kanskje rename til noe annet enn harTilgang?
	fun harTilgang(input: I): Decision

}
