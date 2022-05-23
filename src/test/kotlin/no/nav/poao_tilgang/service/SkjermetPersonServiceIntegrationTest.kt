package no.nav.poao_tilgang.service

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.test_util.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SkjermetPersonServiceIntegrationTest : IntegrationTest() {

	@Autowired
	lateinit var skjermetPersonService: SkjermetPersonService

	@Test
	fun `erSkjermetPerson - skal cache enkelt skjermet person`() {
		val norskIdent = "123879347"

		mockSkjermetPersonHttpClient.enqueueErSkjermet(mapOf(
			norskIdent to true
		))

		mockSkjermetPersonHttpClient.enqueueErSkjermet(mapOf(
			norskIdent to true
		))

		skjermetPersonService.erSkjermetPerson(norskIdent) shouldBe true
		skjermetPersonService.erSkjermetPerson(norskIdent) shouldBe true

		mockSkjermetPersonHttpClient.requestCount() shouldBe 1
	}

	@Test
	fun `erSkjermetPerson - skal cache flere skjermede personer`() {
		val norskIdent1 = "111111111"
		val norskIdent2 = "222222222"
		val norskIdent3 = "333333333"

		mockSkjermetPersonHttpClient.enqueueErSkjermet(mapOf(
			norskIdent1 to true,
			norskIdent2 to false
		))

		val skjerming1 = skjermetPersonService.erSkjermetPerson(listOf(norskIdent1, norskIdent2))

		skjerming1[norskIdent1] shouldBe true
		skjerming1[norskIdent2] shouldBe false
		skjerming1[norskIdent3] shouldBe null

		mockSkjermetPersonHttpClient.enqueueErSkjermet(mapOf(
			norskIdent1 to true,
			norskIdent2 to false,
			norskIdent3 to true,
		))

		val skjerming2 = skjermetPersonService.erSkjermetPerson(listOf(norskIdent1, norskIdent2, norskIdent3))

		skjerming2[norskIdent1] shouldBe true
		skjerming2[norskIdent2] shouldBe false
		skjerming2[norskIdent3] shouldBe true

		mockSkjermetPersonHttpClient.requestCount() shouldBe 2
	}

}
