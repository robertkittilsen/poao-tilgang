package no.nav.poao_tilgang.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.api.dto.DecisionType
import no.nav.poao_tilgang.api.dto.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.api.dto.TilgangResponse
import no.nav.poao_tilgang.client.ClientObjectMapper.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TilgangHttpClient(
	private val baseUrl: String,
	private val authTokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient()
) : TilgangClient {

	override fun harVeilederTilgangTilModia(navIdent: String): Decision {
		val requestJson = objectMapper.writeValueAsString(HarTilgangTilModiaRequest(navIdent))

		val url = joinPaths(baseUrl, "/api/v1/tilgang/modia")

		val request = Request.Builder().url(url).post(requestJson.toRequestBody("application/json".toMediaType()))
			.header("Authorization", "Bearer ${authTokenProvider()}").build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Feilende kall med statuskode ${response.code} mot $url")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			responseToDecision(objectMapper.readValue(body))
		}
	}

	private fun responseToDecision(response: TilgangResponse): Decision {
		return when (response.decision.type) {
			DecisionType.PERMIT -> Decision.Permit
			DecisionType.DENY -> {
				val message = response.decision.message
				val reason = response.decision.reason
				check(message != null) { "message cannot be null" }
				check(reason != null) { "reason cannot be null" }
				Decision.Deny(message, reason)
			}
		}
	}
}
