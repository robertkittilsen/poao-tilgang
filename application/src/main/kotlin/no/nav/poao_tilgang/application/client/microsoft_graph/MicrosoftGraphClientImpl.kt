package no.nav.poao_tilgang.application.client.microsoft_graph

import io.micrometer.core.annotation.Timed
import no.nav.common.rest.client.RestClient.baseClient
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.application.utils.RestUtils.authorization
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NavIdent
import okhttp3.OkHttpClient
import okhttp3.Request

open class MicrosoftGraphClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = baseClient()
) : MicrosoftGraphClient {

	@Timed("microsoft_graph.hent_nav_ident_med_azure_id", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
	override fun hentAdGrupperForNavAnsatt(navAnsattAzureId: AzureObjectId): List<AzureObjectId> {
		val requestData = HentAdGrupperForNavAnsatt.Request(true)

		val request = Request.Builder()
			.url("$baseUrl/v1.0/users/$navAnsattAzureId/getMemberGroups")
			.post(toJsonString(requestData).toJsonRequestBody())
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente Azure Id")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			fromJsonString<HentAdGrupperForNavAnsatt.Response>(body).value
		}
	}

	@Timed("microsoft_graph.hent_ad_grupper", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])

	override fun hentAdGrupper(adGruppeAzureIder: List<AzureObjectId>): List<AdGruppe> {
		val requestData = HentAdGrupper.Request(adGruppeAzureIder)

		val request = Request.Builder()
			.url("$baseUrl/v1.0/directoryObjects/getByIds?\$select=id,displayName")
			.post(toJsonString(requestData).toJsonRequestBody())
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente Azure Id")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			val responseData = fromJsonString<HentAdGrupper.Response>(body)

			responseData.value.map { AdGruppe(it.id, it.displayName) }
		}
	}

	@Timed("microsoft_graph.hent_azure_id_med_nav_identhent_azure_id_med_nav_ident", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
	override fun hentAzureIdMedNavIdent(navIdent: NavIdent): AzureObjectId {
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

			val responseData = fromJsonString<HentAzureIdMedNavIdent.Response>(body)

			responseData.value.firstOrNull()?.id ?: throw RuntimeException("Fant ikke bruker med navIdent=$navIdent")
		}
	}

	@Timed("microsoft_graph.hent_nav_ident_med_azure_id", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
	override fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent {
		val request = Request.Builder()
			.url("$baseUrl/v1.0/users?\$select=onPremisesSamAccountName&\$filter=id eq '$navAnsattAzureId'")
			.get()
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente NAV-ident")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			val responseData = fromJsonString<HentNavIdentMedAzureId.Response>(body)

			responseData.value.firstOrNull()?.onPremisesSamAccountName ?: throw RuntimeException("Fant ikke NAV-ident med Azure Id=$navAnsattAzureId")
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

	object HentAzureIdMedNavIdent {

		data class Response(
			val value: List<UserData>
		) {
			data class UserData(
				val id: AzureObjectId
			)
		}

	}

	object HentNavIdentMedAzureId {

		data class Response(
			val value: List<UserData>
		) {
			data class UserData(
				val onPremisesSamAccountName: NavIdent
			)
		}

	}

}
