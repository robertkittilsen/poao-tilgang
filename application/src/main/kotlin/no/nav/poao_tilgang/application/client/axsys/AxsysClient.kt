package no.nav.poao_tilgang.application.client.axsys

interface AxsysClient {

	fun hentTilganger(navIdent: String): List<EnhetTilgang>

}

data class EnhetTilgang(
	val enhetId: String,
	val enhetNavn: String,
	val temaer: List<String>,
)


