package no.nav.poao_tilgang.poao_tilgang_test_core

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.NavAnsattTilgangTilEksternBrukerPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangMockClient
import no.nav.poao_tilgang.client.TilgangType
import org.junit.jupiter.api.Test


class PoaoTilgangMockClientTest {
	val poaoTilgangMockClient = PoaoTilgangMockClient()
	val navModel = poaoTilgangMockClient.navModel

    @Test
    fun evaluatePolicy() {
		val nyEksternBruker = navModel.nyEksternBruker()
		val nyNksAnsatt = navModel.nyNksAnsatt()

		val tod = NavAnsattTilgangTilEksternBrukerPolicyInput(
			nyNksAnsatt.azureObjectId,
			TilgangType.LESE,
			nyEksternBruker.norskIdent
		)
		val policy = poaoTilgangMockClient.evaluatePolicy(tod).get()!!
		policy.isPermit shouldBe true
	}

    @Test
    fun evaluatePolicies() {

    }

    @Test
    fun hentAdGrupper() {
		val nyNksAnsatt = navModel.nyNksAnsatt()
		val adgrupper = poaoTilgangMockClient.hentAdGrupper(nyNksAnsatt.azureObjectId).get()!!
		adgrupper.size shouldBe nyNksAnsatt.adGrupper.size
		adgrupper.forEachIndexed { index, adGruppe ->
			val annen = nyNksAnsatt.adGrupper.find { it.id == adGruppe.id }
			adGruppe.id shouldBe annen?.id
		}
	}

    @Test
    fun erSkjermetPerson() {
		val skjermetBruker = EksternBruker(erSkjermet = true)
		navModel.leggTilEksternBruker(skjermetBruker)
		val ikkeSkjermet = EksternBruker(erSkjermet = false)
		navModel.leggTilEksternBruker(ikkeSkjermet)

		poaoTilgangMockClient.erSkjermetPerson(skjermetBruker.norskIdent).get() shouldBe true
		poaoTilgangMockClient.erSkjermetPerson(ikkeSkjermet.norskIdent).get() shouldBe false
    }

    @Test
    fun testErSkjermetPerson() {
		val skjermetBruker = EksternBruker(erSkjermet = true)
		navModel.leggTilEksternBruker(skjermetBruker)
		val ikkeSkjermet = EksternBruker(erSkjermet = false)
		navModel.leggTilEksternBruker(ikkeSkjermet)

		val listOf = listOf(skjermetBruker.norskIdent, ikkeSkjermet.norskIdent)
		poaoTilgangMockClient.erSkjermetPerson(listOf).get() shouldBe mapOf(skjermetBruker.norskIdent to true, ikkeSkjermet.norskIdent to false)
	}

}
