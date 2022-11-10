package no.nav.poao_tilgang.client

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.poao_tilgang.client.api.ApiResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PoaoTilgangCachedClientTest {

	lateinit var client: PoaoTilgangClient
	lateinit var cachedClient: PoaoTilgangCachedClient

	@BeforeEach
	fun beforeEach() {
		client = mockk()
		cachedClient = PoaoTilgangCachedClient(client)
	}
	@Test
	fun `evaluatePolicy - policy ikke cached - skal cache og returnere decision`() {
		val input = NavAnsattTilgangTilEksternBrukerPolicyInput("z123", "1321321321")
		every { client.evaluatePolicy(input) } returns ApiResult.success(Decision.Permit)

		val result = cachedClient.evaluatePolicy(input)
		val result2 = cachedClient.evaluatePolicy(input)

		result.get() shouldBe result2.get()
		verify (exactly = 1) { client.evaluatePolicy(any()) }

	}

	@Test
	fun `evaluatePolicy - skal cache riktig input`() {
		val input = NavAnsattTilgangTilEksternBrukerPolicyInput("z123", "1321321321")
		val input2 = NavAnsattTilgangTilModiaPolicyInput("z123")
		val input3 = NavAnsattTilgangTilEksternBrukerPolicyInput("z321", "6464654654")

		every { client.evaluatePolicy(any()) } returns ApiResult.success(Decision.Permit)

		cachedClient.evaluatePolicy(input)
		cachedClient.evaluatePolicy(input2)
		cachedClient.evaluatePolicy(input3)


		verify (exactly = 3) { client.evaluatePolicy(any()) }
	}

}
