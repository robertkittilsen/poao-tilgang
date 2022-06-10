package no.nav.poao_tilgang.core.domain

abstract class Policy<I>(val type: PolicyType) {

	// Kanskje rename til noe annet enn harTilgang?
	abstract fun harTilgang(input: I): Decision

}
