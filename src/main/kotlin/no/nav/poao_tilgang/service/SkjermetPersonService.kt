package no.nav.poao_tilgang.service

import no.nav.poao_tilgang.client.skjermet_person.SkjermetPersonClient
import org.springframework.stereotype.Service

@Service
class SkjermetPersonService(
	private val skjermetPersonClient: SkjermetPersonClient
) {

	fun erSkjermetPerson(norskIdent: String): Boolean {
		val skjerming = skjermetPersonClient.erSkjermet(listOf(norskIdent))
		return skjerming.getOrDefault(norskIdent, false)
	}

	fun erSkjermetPerson(norskeIdenter: List<String>): Map<String, Boolean> {
		return skjermetPersonClient.erSkjermet(norskeIdenter)
	}

}
