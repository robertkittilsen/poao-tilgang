package no.nav.poao_tilgang.poao_tilgang_test_core

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerPolicy
import org.junit.jupiter.api.Test

class NavModellTest {
	val navModell = NavModell()
	val providers = Polecys(navModell)
	val polecyResolver = providers.policyResolver

	@Test
	fun `veileder skal ha tilgang til bruker`() {
		val nyEksternBruker = navModell.nyEksternBruker()
		val veileder = navModell.nyVeilederFor(nyEksternBruker)

		val input = NavAnsattTilgangTilEksternBrukerPolicy.Input(
			navAnsattAzureId = veileder.azureObjectId,
			tilgangType = TilgangType.SKRIVE,
			norskIdent = nyEksternBruker.norskIdent
		)

		val result = polecyResolver.evaluate(input)

		result.decision shouldBe Decision.Permit
	}

	@Test
	fun `nks skal ha tilgang til bruker`() {
		val nyEksternBruker = navModell.nyEksternBruker()
		val nks = navModell.nyNksAnsatt()

		val input = NavAnsattTilgangTilEksternBrukerPolicy.Input(
			navAnsattAzureId = nks.azureObjectId,
			tilgangType = TilgangType.SKRIVE,
			norskIdent = nyEksternBruker.norskIdent
		)

		val result = polecyResolver.evaluate(input)

		result.decision shouldBe Decision.Permit
	}

}
