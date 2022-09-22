package no.nav.poao_tilgang.application.client.pdl

import no.nav.common.rest.client.RestClient.baseClient
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PdlClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val httpClient: OkHttpClient = baseClient(),
) : PdlClient {

	private val mediaTypeJson = "application/json".toMediaType()

	override fun hentBrukerInfo(brukerIdent: String): BrukerInfo {
		val requestBody = toJsonString(
			Graphql.GraphqlQuery(
				PdlQueries.HentBrukerInfo.query,
				PdlQueries.HentBrukerInfo.Variables(brukerIdent)
			)
		)

		val request = createGraphqlRequest(requestBody)

		httpClient.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente informasjon fra PDL. Status: ${response.code}")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing from PDL request")

			val gqlResponse = fromJsonString<PdlQueries.HentBrukerInfo.Response>(body)

			throwPdlApiErrors(gqlResponse) // respons kan inneholde feil selv om den ikke er tom ref: https://pdldocs-navno.msappproxy.net/ekstern/index.html#appendix-graphql-feilhandtering

			if (gqlResponse.data == null) {
				throw RuntimeException("PDL respons inneholder ikke data")
			}

			return toBrukerInfo(gqlResponse.data)
		}
	}

	private fun createGraphqlRequest(jsonPayload: String): Request {
		return Request.Builder()
			.url("$baseUrl/graphql")
			.addHeader("Authorization", "Bearer ${tokenProvider()}")
			.addHeader("Tema", "GEN")
			.post(jsonPayload.toRequestBody(mediaTypeJson))
			.build()
	}

	private fun toBrukerInfo(hentBrukerInfo: PdlQueries.HentBrukerInfo.ResponseData): BrukerInfo {
		val geografiskTilknytning = hentBrukerInfo.hentGeografiskTilknytning

		return BrukerInfo(
			adressebeskyttelse = getAdressebeskyttelse(hentBrukerInfo.hentPerson.adressebeskyttelse),
			geografiskTilknytning = geografiskTilknytning?.let {
				GeografiskTilknytning(
					gtType = it.gtType,
					gtBydel = it.gtBydel,
					gtKommune = it.gtKommune,
				)
			}
		)
	}

	private fun getAdressebeskyttelse(adressebeskyttelse: List<PdlQueries.HentBrukerInfo.Adressebeskyttelse>): Adressebeskyttelse? {
		return when(adressebeskyttelse.firstOrNull()?.gradering) {
			"STRENGT_FORTROLIG_UTLAND" -> Adressebeskyttelse.STRENGT_FORTROLIG_UTLAND
			"STRENGT_FORTROLIG" -> Adressebeskyttelse.STRENGT_FORTROLIG
			"FORTROLIG" -> Adressebeskyttelse.FORTROLIG
			else -> null
		}
	}

	private fun throwPdlApiErrors(response: PdlQueries.HentBrukerInfo.Response) {
		var melding = "Feilmeldinger i respons fra pdl:\n"
		if(response.data == null) melding = "$melding- data i respons er null \n"
		response.errors?.let { feilmeldinger ->
			melding += feilmeldinger.joinToString(separator = "") { "- ${it.message} (code: ${it.extensions?.code} details: ${it.extensions?.details})\n" }
			throw RuntimeException(melding)

		}
	}

}
