package no.nav.poao_tilgang.core.domain

interface Policy<I : PolicyInput> {

	val name: String

	fun evaluate(input: I): Decision

}
