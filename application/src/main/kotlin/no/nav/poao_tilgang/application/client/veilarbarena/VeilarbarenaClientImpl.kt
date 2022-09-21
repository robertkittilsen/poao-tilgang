package no.nav.poao_tilgang.application.client.veilarbarena

import no.nav.common.rest.client.RestClient.baseClient
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.SecureLog.secureLog
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.OkHttpClient
import okhttp3.Request

class VeilarbarenaClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val httpClient: OkHttpClient = baseClient(),
	private val consumerId: String,
) : VeilarbarenaClient {

	override fun hentBrukerOppfolgingsenhetId(norskIdent: NorskIdent): NavEnhetId? {
		val request = Request.Builder()
			.url("$baseUrl/api/arena/status?fnr=$norskIdent")
			.addHeader("Authorization", "Bearer ${tokenProvider()}")
			.addHeader("Nav-Consumer-Id", consumerId)
			.get()
			.build()

		httpClient.newCall(request).execute().use { response ->
			if (response.code == 404) {
				secureLog.warn("Fant ikke bruker med fnr=$norskIdent i veilarbarena")
				return null
			}

			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente status fra veilarbarena. Status: ${response.code}")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			val statusDto = fromJsonString<BrukerArenaStatusDto>(body)

			return statusDto.oppfolgingsenhet
		}
	}

	private data class BrukerArenaStatusDto(
		var oppfolgingsenhet: String?
	)

}
