package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.TilgangType

interface AbacProvider {

	fun harVeilederTilgangTilPerson(
		veilederIdent: String,
		tilgangType: TilgangType,
		eksternBrukerId: String
	): Boolean

	fun harVeilederTilgangTilNavEnhet(
		veilederIdent: String,
		navEnhetId: String
	): Boolean

}
