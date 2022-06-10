package no.nav.poao_tilgang.core.provider

interface SkjermetPersonProvider {

	fun erSkjermetPerson(norskIdent: String): Boolean

}
