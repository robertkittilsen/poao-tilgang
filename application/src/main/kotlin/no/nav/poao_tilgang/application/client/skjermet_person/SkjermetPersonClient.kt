package no.nav.poao_tilgang.application.client.skjermet_person

interface SkjermetPersonClient {

	fun erSkjermet(norskeIdenter: List<String>): Map<String, Boolean>

}
