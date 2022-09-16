package no.nav.poao_tilgang.application.client.axsys

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CachedAxsysClientTest {

	@Test
	fun `hentTilganger - kaller to ganger - delegate blir bare kalt en gang og resultatet er likt`() {
		val axsysClient = mockk<AxsysClient>()

		val cachedClient = CachedAxsysClient(axsysClient)

		val brukerIdent = "AB12345"

		val enheter = listOf(
			EnhetTilgang(
				enhetId = "1234",
				temaer = listOf("ABC", "DEF"),
				enhetNavn = "Bygdeby"
			)
		)

		every {
			axsysClient.hentTilganger(brukerIdent)
		} returns enheter

		val response = cachedClient.hentTilganger(brukerIdent)
		val response2 = cachedClient.hentTilganger(brukerIdent)

		assertEquals(enheter, response)
		assertEquals(enheter, response2)

		verify(exactly = 1) { axsysClient.hentTilganger(brukerIdent) }
	}

}
