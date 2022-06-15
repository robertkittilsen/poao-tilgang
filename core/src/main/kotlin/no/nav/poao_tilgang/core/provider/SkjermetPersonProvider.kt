package no.nav.poao_tilgang.core.provider

interface SkjermetPersonProvider {

	fun erSkjermetPerson(norskIdent: String): Boolean

	fun erSkjermetPerson(norskeIdenter: List<String>): Map<String, Boolean>

}
