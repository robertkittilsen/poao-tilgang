package no.nav.poao_tilgang.application.provider

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class AdGruppeProviderImplIntegrationTest : IntegrationTest() {

	@Autowired
	lateinit var adGruppeProvider: AdGruppeProvider

	@Test
	fun `hentAdGrupper - skal cache kall til ms graph`() {
		val init = mockMicrosoftGraphHttpServer.requestCount()
		val navAnsattAzureId = UUID.randomUUID()

		val adGroupId1 = UUID.randomUUID()
		val adGroupId2 = UUID.randomUUID()

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(
			navAnsattAzureId, listOf(adGroupId1, adGroupId2)
		)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(
			listOf(AdGruppe(adGroupId1, "Gruppe1"), AdGruppe(adGroupId2, "Gruppe2"))
		)

		val adGrupper = adGruppeProvider.hentAdGrupper(navAnsattAzureId)

		adGrupper.size shouldBe 2
		adGrupper.any { it.id == adGroupId1 && it.navn == "Gruppe1" } shouldBe true
		adGrupper.any { it.id == adGroupId2 && it.navn == "Gruppe2" } shouldBe true

		adGruppeProvider.hentAdGrupper(navAnsattAzureId).size shouldBe 2

		mockMicrosoftGraphHttpServer.requestCount() - init  shouldBe 2
	}

}
