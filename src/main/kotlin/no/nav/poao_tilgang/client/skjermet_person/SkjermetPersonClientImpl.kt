package no.nav.poao_tilgang.client.skjermet_person

import no.nav.common.rest.client.RestClient
import no.nav.poao_tilgang.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.utils.RestUtils.toJsonRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request

class SkjermetPersonClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient()
) : SkjermetPersonClient {

	override fun erSkjermet(norskeIdenter: List<String>): Map<String, Boolean> {
		val requestJson = toJsonString(SkjermetPersonBulkRequest(norskeIdenter))

		val request = Request.Builder()
			.url( "$baseUrl/skjermetBulk")
			.post(requestJson.toJsonRequestBody())
			.header("Authorization", "Bearer ${tokenProvider.invoke()}")
			.build()

		return client.newCall(request).execute().let { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente skjermet person bolk")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			fromJsonString(body)
		}
	}

	data class SkjermetPersonBulkRequest(
		val personidenter: List<String>
	)

}
