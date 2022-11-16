package no.nav.poao_tilgang.client

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.poao_tilgang.client.api.ApiResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class PoaoTilgangCachedClientTest {

	lateinit var client: PoaoTilgangClient
	lateinit var cachedClient: PoaoTilgangCachedClient

	@BeforeEach
	fun beforeEach() {
		client = mockk()
		cachedClient = PoaoTilgangCachedClient(client)
	}

	@Test
	fun `evaluatePolicy - skal cache og returnere decision`() {
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

	@Test
	fun `hentAdGrupper - skal cache på riktig id`() {
		val ansattId1 = UUID.randomUUID()
		val ansattId2 = UUID.randomUUID()

		val grupper1 = listOf(AdGruppe(UUID.randomUUID(),"grp1"))
		val grupper2 = listOf(AdGruppe(UUID.randomUUID(),"grp2"))

		every { client.hentAdGrupper(ansattId1) } returns ApiResult.success(grupper1)
		every { client.hentAdGrupper(ansattId2) } returns ApiResult.success(grupper2)

		cachedClient.hentAdGrupper(ansattId1).getOrThrow() shouldBe grupper1
		cachedClient.hentAdGrupper(ansattId1).getOrThrow() shouldBe grupper1
		cachedClient.hentAdGrupper(ansattId2).getOrThrow() shouldBe grupper2

		verify (exactly = 2) { client.hentAdGrupper(any()) }
	}

	@Test
	fun `erSkjermetPerson (enkeltperson) - skal cache på riktig id`() {
		val norskIdent1 = "fnr1"
		val norskIdent2 = "fnr2"

		every { client.erSkjermetPerson(norskIdent1) } returns ApiResult.success(true)
		every { client.erSkjermetPerson(norskIdent2) } returns ApiResult.success(false)

		cachedClient.erSkjermetPerson(norskIdent1).getOrThrow() shouldBe true
		cachedClient.erSkjermetPerson(norskIdent1).getOrThrow() shouldBe true
		cachedClient.erSkjermetPerson(norskIdent2).getOrThrow() shouldBe false

		verify (exactly = 2) { client.erSkjermetPerson(any<String>()) }
	}

	@Test
	fun `erSkjermetPerson (bulk) - skal sjekke tidligere cachede verdier før request sendes til underliggende klient`() {
		val norskIdent1 = "fnr1"
		val norskIdent2 = "fnr2"
		val norskIdent3 = "fnr3"

		val res1 = mapOf(norskIdent1 to true, norskIdent2 to false)

		every {
			client.erSkjermetPerson(listOf(norskIdent1, norskIdent2))
		} returns ApiResult.success(res1)

		every {
			client.erSkjermetPerson(listOf(norskIdent3))
		} returns ApiResult.success(mapOf(norskIdent3 to true))

		cachedClient.erSkjermetPerson(listOf(norskIdent1, norskIdent2)).getOrThrow() shouldBe res1
		cachedClient.erSkjermetPerson(listOf(norskIdent1, norskIdent2)).getOrThrow() shouldBe res1

		cachedClient.erSkjermetPerson(listOf(norskIdent1, norskIdent2, norskIdent3)).getOrThrow() shouldBe mapOf(
			norskIdent1 to true, norskIdent2 to false, norskIdent3 to true
		)

		verify (exactly = 1) { client.erSkjermetPerson(listOf(norskIdent1, norskIdent2)) }
		verify (exactly = 1) { client.erSkjermetPerson(listOf(norskIdent3)) }
	}

}
