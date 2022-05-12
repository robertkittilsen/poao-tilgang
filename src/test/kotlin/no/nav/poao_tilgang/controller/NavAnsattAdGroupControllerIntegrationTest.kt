package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.test_util.MockOAuthServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NavAnsattAdGroupControllerIntegrationTest {

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient()

	private val mockOAuthServer = MockOAuthServer()

	private fun serverUrl() = "http://localhost:$port"

	@Test
	fun `hentAdGrupperForNavAnsatt - should return 401 when not authenticated`() {
		val response = client.newCall(
			Request.Builder()
				.url("${serverUrl()}/api/v1/ad-group?navIdent=Z1234")
				.get()
				.build()
		).execute()

		assertEquals(401, response.code)
	}

	@Test
	fun `hentAdGrupperForNavAnsatt - should return 200 when authenticated`() {
		val response = client.newCall(
			Request.Builder()
				.url("${serverUrl()}/api/v1/ad-group?navIdent=Z1234")
				.addHeader("Authorization", "Bearer ${mockOAuthServer.azureAdToken()}")
				.get()
				.build()
		).execute()

		val expectedJson = """
			[]
		""".trimIndent()

		assertEquals(expectedJson, response.body?.string())
		assertEquals(200, response.code)
	}

}
