package no.nav.poao_tilgang.application.client.microsoft_graph

import no.nav.common.rest.client.RestClient.baseClient
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.application.utils.RestUtils.authorization
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import no.nav.poao_tilgang.core.domain.AzureObjectId
import okhttp3.OkHttpClient
import okhttp3.Request

class MicrosoftGraphClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = baseClient()
) : MicrosoftGraphClient {

	override fun hentAdGrupperForNavAnsatt(navAnsattAzureId: AzureObjectId): List<AzureObjectId> {
		val requestData = HentAdGrupperForNavAnsatt.Request(true)

		val request = Request.Builder()
			.url("$baseUrl/v1.0/users/$navAnsattAzureId/getMemberGroups")
			.post(toJsonString(requestData).toJsonRequestBody())
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().let { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente Azure Id")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			fromJsonString<HentAdGrupperForNavAnsatt.Response>(body).value
		}
	}

	override fun hentAdGrupper(adGruppeAzureIder: List<AzureObjectId>): List<AdGruppe> {
		val requestData = HentAdGrupper.Request(adGruppeAzureIder)

		val request = Request.Builder()
			.url("$baseUrl/v1.0/directoryObjects/getByIds?\$select=id,displayName")
			.post(toJsonString(requestData).toJsonRequestBody())
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().let { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente Azure Id")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			val responseData = fromJsonString<HentAdGrupper.Response>(body)

			responseData.value.map { AdGruppe(it.id, it.displayName) }
		}
	}

	override fun hentAzureIdForNavAnsatt(navIdent: String): AzureObjectId {
		val request = Request.Builder()
			.url("$baseUrl/v1.0/users?\$select=id&\$count=true&\$filter=onPremisesSamAccountName eq '$navIdent'")
			.header("ConsistencyLevel", "eventual")
			.get()
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente Azure Id")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			val responseData = fromJsonString<HentAzureIdForNavAnsatt.Response>(body)

			responseData.value.firstOrNull()?.id ?: throw RuntimeException("Fant ikke bruker med navIdent=$navIdent")
		}
	}

	object HentAdGrupper {

		data class Request(
			val ids: List<AzureObjectId>,
			val types: List<String> = listOf("group")
		)

		data class Response(
			val value: List<AdGruppe>
		) {
			data class AdGruppe(
				val id: AzureObjectId,
				val displayName: String
			)
		}

	}

	object HentAdGrupperForNavAnsatt {

		data class Request(
			val securityEnabledOnly: Boolean
		)

		data class Response(
			val value: List<AzureObjectId>
		)

	}

	object HentAzureIdForNavAnsatt {

		data class Response(
			val value: List<UserData>
		) {
			data class UserData(
				val id: AzureObjectId
			)
		}

	}

}
