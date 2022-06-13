package no.nav.poao_tilgang.service

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.test_util.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class AdGruppeProviderImplIntegrationTest : IntegrationTest() {

	@Autowired
	lateinit var adGruppeProvider: AdGruppeProvider

	@Test
	fun `hentAdGrupper - skal cache kall til ms graph`() {
		val navIdent = "Z12371"
		val navAnsattAzureId = UUID.randomUUID()

		val adGroupId1 = UUID.randomUUID()
		val adGroupId2 = UUID.randomUUID()

		mockMicrosoftGraphHttpClient.enqueueHentAzureIdForNavAnsattResponse(
			navAnsattAzureId
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperForNavAnsatt(
			listOf(adGroupId1, adGroupId2)
		)

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperResponse(
			listOf(AdGruppe(adGroupId1, "Gruppe1"), AdGruppe(adGroupId2, "Gruppe2"))
		)

		val adGrupper = adGruppeProvider.hentAdGrupper(navIdent)

		adGrupper.size shouldBe 2
		adGrupper.any { it.id == adGroupId1 && it.name == "Gruppe1" } shouldBe true
		adGrupper.any { it.id == adGroupId2 && it.name == "Gruppe2" } shouldBe true

		adGruppeProvider.hentAdGrupper(navIdent).size shouldBe 2

		mockMicrosoftGraphHttpClient.requestCount() shouldBe 3
	}

}
