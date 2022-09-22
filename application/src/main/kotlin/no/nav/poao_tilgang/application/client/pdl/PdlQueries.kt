package no.nav.poao_tilgang.application.client.pdl

object PdlQueries {

	data class PdlError (
		override val message: String? = null,
		override val locations: List<Graphql.GraphqlErrorLocation>? = null,
		override val path: List<String>? = null,
		override val extensions: PdlErrorExtension? = null,
	): Graphql.GraphqlError<PdlErrorExtension>

	data class PdlErrorExtension(
		val code: String? = null,
		val classification: String? = null,
		val details: PdlErrorDetails? = null
	)

	data class PdlErrorDetails(
		val type: String? = null,
		val cause: String? = null,
		val policy: String? = null
	)

	object HentBrukerInfo {
		val query = """
			query(${"$"}ident: ID!) {
			  hentPerson(ident: ${"$"}ident) {
				adressebeskyttelse(historikk: false) {
				  gradering
				}
			  }
			  hentGeografiskTilknytning(ident: ${"$"}ident) {
				gtType
				gtKommune
				gtBydel
			  }
			}
		""".trimIndent()

		data class Variables(
			val ident: String,
		)

		data class Response(
			override val errors: List<PdlError>?,
			override val data: ResponseData?
		) : Graphql.GraphqlResponse<ResponseData, PdlErrorExtension>

		data class ResponseData(
			val hentPerson: HentPerson,
			val hentGeografiskTilknytning: HentGeografiskTilknytning?,
		)

		data class HentPerson(
			val adressebeskyttelse: List<Adressebeskyttelse>
		)

		data class Adressebeskyttelse(
			val gradering: String?
		)

		data class HentGeografiskTilknytning(
			val gtType: String,
			val gtKommune: String?,
			val gtBydel: String?,
		)
	}

}
