package no.nav.poao_tilgang.application.client.norg

import io.micrometer.core.annotation.Timed
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.SecureLog
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.OkHttpClient
import okhttp3.Request

open class NorgHttpClient(
	private val baseUrl: String,
	private val httpClient: OkHttpClient = RestClient.baseClient(),
) : NorgClient {

	@Timed("norg_http_client.hent_tilhorende_enhet", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
	override fun hentTilhorendeEnhet(
		geografiskTilknytning: String,
	): NavEnhetId? {

		val requestBuilder = Request.Builder()
			.url(joinPaths(baseUrl, "/norg2/api/v1/enhet/navkontor/", geografiskTilknytning))
			.get()

		val request = requestBuilder.build()

		httpClient.newCall(request).execute().use { response ->

			if (!response.isSuccessful) {
				if (response.code == 404) {
						SecureLog.secureLog.info("Fant ikke NAV-enhet basert på geografisk tilknytning = $geografiskTilknytning i Norg.")
						return null
					}
				} else {
					throw RuntimeException(
						"Klarte ikke å hente NAV-enhet basert på geografisk tilknytning = $geografiskTilknytning fra Norg. Status: ${response.code}"
					)
				}


			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			val enhetResponse = fromJsonString<EnhetResponse>(body)
			return enhetResponse.enhetNr
		}
	}

	private data class EnhetResponse(val enhetNr: String)
}
