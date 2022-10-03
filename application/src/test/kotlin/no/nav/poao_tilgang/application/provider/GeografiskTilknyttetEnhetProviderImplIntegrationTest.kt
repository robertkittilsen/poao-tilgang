package no.nav.poao_tilgang.application.provider

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GeografiskTilknyttetEnhetProviderImplIntegrationTest : IntegrationTest() {

	@Autowired
	lateinit var geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider

	@Test
	fun `henter tilhørende enhet basert på geografisk tilknyting kommune`() {
		mockPdlHttpServer.mockBrukerInfo(
			norskIdent = "987", gtType = "KOMMUNE", gtKommune = "0570"
		)
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning = "0570", tilhorendeEnhet = "1234")

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet("987") shouldBe "1234"
	}

	@Test
	fun `henter tilhørende enhet basert på geografisk tilknyting bydel`() {
		mockPdlHttpServer.mockBrukerInfo(
			norskIdent = "988", gtType = "BYDEL", gtBydel = "057021"
		)
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning = "057021", tilhorendeEnhet = "1235")

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet("988") shouldBe "1235"
	}

	@Test
	fun `kan ikke hente enhet basert på geografisk tilknyting utland`() {
		mockPdlHttpServer.mockBrukerInfo(
			norskIdent = "989", gtType = "UTLAND"
		)

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet("989") shouldBe null
	}

	@Test
	fun `feiler dersom tilhørende enhet for geografisk tilknyting ikke finnes`() {
		mockPdlHttpServer.mockBrukerInfo(
			norskIdent = "990", gtType = "KOMMUNE", gtKommune = "9999"
		)

		mockNorgHttpServer.mockIngenTilhorendeEnhet("9999")

		shouldThrow<RuntimeException> {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet("990")
		}
	}
}
