package no.nav.poao_tilgang.application.client.veilarbarena

import io.micrometer.core.annotation.Timed
import no.nav.common.rest.client.RestClient.baseClient
import no.nav.common.rest.client.RestUtils.MEDIA_TYPE_JSON
import no.nav.poao_tilgang.application.utils.JsonUtils
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.SecureLog.secureLog
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

open class VeilarbarenaClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val httpClient: OkHttpClient = baseClient(),
	private val consumerId: String,
) : VeilarbarenaClient {

	@Timed("veilarbarena_client.hent_bruker_oppfolgingsenhet_id", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
	override fun hentBrukerOppfolgingsenhetId(personRequest: PersonRequest): NavEnhetId? {
		val personRequestJSON = JsonUtils.toJsonString(personRequest)
		val requestBody = personRequestJSON.toRequestBody(MEDIA_TYPE_JSON)
		val request = Request.Builder()
			.url("$baseUrl/api/v2/arena/hent-status")
			.addHeader("Authorization", "Bearer ${tokenProvider()}")
			.addHeader("Nav-Consumer-Id", consumerId)
			.post(requestBody)
			.build()

		httpClient.newCall(request).execute().use { response ->
			if (response.code == 404) {
				secureLog.warn("Fant ikke bruker med fnr=${personRequest.fnr} i veilarbarena")
				return null
			}

			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente status fra veilarbarena. Status: ${response.code}")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			secureLog.info("Veilarbarena response, hentOppfolgingsEnhetId for norskIdent: ${personRequest.fnr}, body: $body")

			val statusDto = fromJsonString<BrukerArenaStatusDto>(body)

			return statusDto.oppfolgingsenhet
		}
	}

	private data class BrukerArenaStatusDto(
		var oppfolgingsenhet: String?
	)

}
