package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.PoaoTilgangClient
import no.nav.poao_tilgang.client.PoaoTilgangHttpClient
import no.nav.poao_tilgang.poao_tilgang_test_core.tilgjengligeAdGrupper
import org.junit.jupiter.api.Test


class Poao_wiermockTest {
	val managedWiermock = Managed_wiermock()
	val baseUrl = managedWiermock.wireMockServer.baseUrl()
	val navModell = managedWiermock.poaoWiermock.navModell
	val nyEksternBruker = navModell.nyEksternBruker()

	val poaoTilgangHttpClient: PoaoTilgangClient = PoaoTilgangHttpClient(baseUrl, { "kake" })


	@Test
	fun skjermet_person() {
		val erSkjermetPersonFalse = poaoTilgangHttpClient.erSkjermetPerson(nyEksternBruker.norskIdent)
		erSkjermetPersonFalse.get() shouldBe false

		nyEksternBruker.erSkjermet = true
		val erSkjermetPersonTrue = poaoTilgangHttpClient.erSkjermetPerson(nyEksternBruker.norskIdent)
		erSkjermetPersonTrue.get() shouldBe true

	}

	@Test
	fun skal_hente_adGrupper() {
		val nyNksAnsatt = navModell.nyNksAnsatt()
		val anttal_roller = nyNksAnsatt.adGrupper.size
		val hentAdGrupper = poaoTilgangHttpClient.hentAdGrupper(nyNksAnsatt.azureObjectId)
		hentAdGrupper.get()!!.size shouldBe nyNksAnsatt.adGrupper.size


		nyNksAnsatt.adGrupper.add(tilgjengligeAdGrupper.aktivitetsplanKvp)
		val hentAdGrupper_pluss_1 = poaoTilgangHttpClient.hentAdGrupper(nyNksAnsatt.azureObjectId)
		hentAdGrupper_pluss_1.get()!!.size shouldBe nyNksAnsatt.adGrupper.size

		nyNksAnsatt.adGrupper.size shouldBe  anttal_roller +1 // sjekk at vi har lagt til i modellen
	}




}
