package no.nav.poao_tilgang.core.domain

import java.util.*

typealias AdGruppeNavn = String

data class AdGruppe(
	val id: UUID,
	val name: AdGruppeNavn
)
