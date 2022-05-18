package no.nav.poao_tilgang.client.microsoft_graph

import no.nav.common.rest.client.RestClient.baseClient
import no.nav.poao_tilgang.domain.AzureObjectId
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.*

class MicrosoftGraphClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = baseClient()
) : MicrosoftGraphClient {

	override fun hentAdGrupper(azureId: AzureObjectId): List<AdGruppe> {
		val request = Request.Builder()
			.url( "$baseUrl/v1.0/user/$azureId/getMemberGroups")
			.get()
			.header("Authorization", "Bearer ${tokenProvider.invoke()}")
			.build()

		return client.newCall(request).execute().let {
			val body = it.body?.string()

			println(body)

			emptyList()
		}
	}

	override fun hentAzureId(navIdent: String): AzureObjectId {

		val request = Request.Builder()
			.url("$baseUrl/v1.0/users?\$select=id&\$filter=${URLEncoder.encode("mailnickname eq '$navIdent'", Charsets.UTF_8)}")
			.get()
			.header("Authorization", "Bearer ${tokenProvider.invoke()}")
			.build()

		client.newCall(request).execute().let {

			val body = it.body?.string()

			println(body)

			return UUID.randomUUID()
		}
	}

}
