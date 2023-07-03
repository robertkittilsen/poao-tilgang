package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.Decision
import no.nav.poao_tilgang.client.NavAnsattTilgangTilModiaPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangClient
import no.nav.poao_tilgang.client.PoaoTilgangHttpClient
import no.nav.poao_tilgang.poao_tilgang_test_core.NavAnsatt
import no.nav.poao_tilgang.poao_tilgang_test_core.tilgjengligeAdGrupper
import org.junit.jupiter.api.Test


class Poao_wiermockTest {
	val managedWiermock = PoaoTilgangWiremock()
	val baseUrl = managedWiermock.wireMockServer.baseUrl()
	val navContext = managedWiermock.navContext

	val poaoTilgangHttpClient: PoaoTilgangClient = PoaoTilgangHttpClient(baseUrl, { "kake" })


	@Test
	fun skjermet_person() {
		val privatBruker = navContext.privatBrukere.ny()
		val erSkjermetPersonFalse = poaoTilgangHttpClient.erSkjermetPerson(privatBruker.norskIdent)
		erSkjermetPersonFalse.get() shouldBe false

		privatBruker.erSkjermet = true
		val erSkjermetPersonTrue = poaoTilgangHttpClient.erSkjermetPerson(privatBruker.norskIdent)
		erSkjermetPersonTrue.get() shouldBe true

	}

	@Test
	fun skal_hente_adGrupper() {
		val nyNksAnsatt = navContext.navAnsatt.nyNksAnsatt()
		val anttal_roller = nyNksAnsatt.adGrupper.size
		val hentAdGrupper = poaoTilgangHttpClient.hentAdGrupper(nyNksAnsatt.azureObjectId)
		hentAdGrupper.get()!!.size shouldBe nyNksAnsatt.adGrupper.size

		nyNksAnsatt.adGrupper.add(tilgjengligeAdGrupper.aktivitetsplanKvp)
		val hentAdGrupper_pluss_1 = poaoTilgangHttpClient.hentAdGrupper(nyNksAnsatt.azureObjectId)
		hentAdGrupper_pluss_1.get()!!.size shouldBe nyNksAnsatt.adGrupper.size

		withClue("sjekk at vi har lagt til i modellen") {
			nyNksAnsatt.adGrupper.size shouldBe  anttal_roller +1
		}
	}


	@Test
	fun skal_evaluere_polecy() {
		val nyNksAnsatt = navContext.navAnsatt.nyNksAnsatt()
		val premitDesicion =
			poaoTilgangHttpClient.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(nyNksAnsatt.azureObjectId))

		premitDesicion.get() shouldBe Decision.Permit

		val utenTilgang = NavAnsatt()
		navContext.navAnsatt.add(utenTilgang)

		poaoTilgangHttpClient.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(utenTilgang.azureObjectId)).get()?.isDeny shouldBe true
	}

}
