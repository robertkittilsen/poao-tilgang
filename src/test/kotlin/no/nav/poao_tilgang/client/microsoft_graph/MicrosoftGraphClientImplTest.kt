package no.nav.poao_tilgang.client.microsoft_graph

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.test_util.MockHttpClient
import java.util.*

class MicrosoftGraphClientImplTest : FunSpec({

	val mockClient = MockHttpClient()

	afterSpec {
		mockClient.shutdown()
	}

	test("hentAdGrupper - skal lage riktig request og parse respons") {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockClient.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val adGruppeId = UUID.randomUUID()

		mockClient.enqueue(
			body = """
					{
						"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#directoryObjects(id,displayName)",
						"value": [
							{
								"@odata.type": "#microsoft.graph.group",
								"id": "$adGruppeId",
								"displayName": "Test"
							}
						]
					}
				""".trimIndent()
		)

		val adGrupper = client.hentAdGrupper(listOf(adGruppeId))

		adGrupper.first().id shouldBe adGruppeId
		adGrupper.first().name shouldBe "Test"

		val request = mockClient.latestRequest()

		request.path shouldBe "/v1.0/directoryObjects/getByIds?\$select=id,displayName"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		val expectedRequestJson = """
			{"ids":["$adGruppeId"],"types":["group"]}
		""".trimIndent()

		request.body.readUtf8() shouldBe expectedRequestJson
	}

	test("hentAdGrupperForNavAnsatt - skal lage riktig request og parse respons") {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockClient.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val navAnsattAzureId = UUID.randomUUID()
		val adGroupAzureId = UUID.randomUUID()

		mockClient.enqueue(
			body = """
					{
						"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#Collection(Edm.String)",
						"value": [
							"$adGroupAzureId"
						]
					}
				""".trimIndent()
		)

		val adGrupper = client.hentAdGrupperForNavAnsatt(navAnsattAzureId)

		adGrupper.first() shouldBe adGroupAzureId

		val request = mockClient.latestRequest()

		request.path shouldBe "/v1.0/user/$navAnsattAzureId/getMemberGroups"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		val expectedRequestJson = """
			{"securityEnabledOnly":true}
		""".trimIndent()

		request.body.readUtf8() shouldBe expectedRequestJson
	}

	test("hentAzureIdForNavAnsatt - skal lage riktig request og parse respons") {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockClient.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val navIdent = "Z1234"
		val navAnsattAzureId = UUID.randomUUID()

		mockClient.enqueue(
			body = """
				{
					"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#users(id)",
					"value": [
						{
							"id": "$navAnsattAzureId"
						}
					]
				}
				""".trimIndent()
		)

		val azureId = client.hentAzureIdForNavAnsatt(navIdent)

		azureId shouldBe navAnsattAzureId

		val request = mockClient.latestRequest()

		request.path shouldBe "/v1.0/users?\$select=id&\$filter=mailNickname%20eq%20%27$navIdent%27"
		request.method shouldBe "GET"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		request.body.readUtf8() shouldBe ""
	}

})
