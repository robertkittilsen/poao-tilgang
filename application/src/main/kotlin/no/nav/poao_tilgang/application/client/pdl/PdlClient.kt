package no.nav.poao_tilgang.application.client.pdl

interface PdlClient {

	fun hentBrukerInfo(brukerIdent: String): BrukerInfo

}

data class BrukerInfo(
	val adressebeskyttelse: Adressebeskyttelse?,
	val geografiskTilknytning: GeografiskTilknytning?
)

enum class Adressebeskyttelse {
	STRENGT_FORTROLIG,
	FORTROLIG,
	STRENGT_FORTROLIG_UTLAND,
}

data class GeografiskTilknytning(
	val gtType: String,
	val gtKommune: String? = null,
	val gtBydel: String? = null,
)
