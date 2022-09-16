package no.nav.poao_tilgang.application.client.skjermet_person

import no.nav.common.rest.client.RestClient.baseClient
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.application.utils.RestUtils.authorization
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request

class SkjermetPersonClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = baseClient()
) : SkjermetPersonClient {

	override fun erSkjermet(norskeIdenter: List<String>): Map<String, Boolean> {
		val requestJson = toJsonString(ErSkjermet.Request(norskeIdenter))

		val request = Request.Builder()
			.url( "$baseUrl/skjermetBulk")
			.post(requestJson.toJsonRequestBody())
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente skjermet person bolk")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			fromJsonString(body)
		}
	}

	object ErSkjermet {

		data class Request(
			val personidenter: List<String>
		)

	}

}
