package no.nav.poao_tilgang.poao_tilgang_test_core

import no.nav.poao_tilgang.core.domain.NavEnhetId


private val navidener: HashSet<String> = hashSetOf()

fun nyNavIdent(): String {
	val navIdent = ('a'..'z').random() + (100000..999999).random().toString()
	if (navidener.contains(navIdent)) {
		return nyNavIdent()
	}
	navidener.add(navIdent)
	return navIdent
}


private val enheter = (1000..9999).shuffled().toMutableList()
fun nyNavEnhet(): NavEnhetId {
	return enheter.removeFirst().toString()
}
