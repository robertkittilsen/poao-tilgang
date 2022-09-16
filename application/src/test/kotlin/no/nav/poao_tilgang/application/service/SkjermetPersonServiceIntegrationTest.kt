package no.nav.poao_tilgang.application.service

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.provider.SkjermetPersonProvider
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SkjermetPersonServiceIntegrationTest : IntegrationTest() {

	@Autowired
	lateinit var skjermetPersonProvider: SkjermetPersonProvider

	@Test
	fun `erSkjermetPerson - skal defaulte til true hvis data mangler`() {
		val norskIdent = "123879347"

		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf())

		skjermetPersonProvider.erSkjermetPerson(norskIdent) shouldBe true
	}

	@Test
	fun `erSkjermetPerson - skal cache enkelt skjermet person`() {
		val norskIdent = "123879347"

		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(
			norskIdent to true
		))

		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(
			norskIdent to true
		))

		skjermetPersonProvider.erSkjermetPerson(norskIdent) shouldBe true
		skjermetPersonProvider.erSkjermetPerson(norskIdent) shouldBe true

		mockSkjermetPersonHttpServer.requestCount() shouldBe 1
	}

}
