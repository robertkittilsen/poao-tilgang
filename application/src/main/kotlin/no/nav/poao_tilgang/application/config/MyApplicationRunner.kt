package no.nav.poao_tilgang.application.config

import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.util.UUID

@Component
/**
 * Det er et problem at de første kallene mot microsoft graph serveren timer ut rett etter oppstart av serveren.
 * Bønnen kaller microsoft graph serveren under oppstart av applikasjonen, for å forsøke å 'varme opp' klienten.
 * Klassen er 'open' slik at bønnen kan mockes bort i IntegrationTest.kt
 */
open class MyApplicationRunner(private val microsoftClient: MicrosoftGraphClient): ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		try {
			microsoftClient.hentNavIdentMedAzureId(UUID.randomUUID())
		} catch (e: Exception) {
			//denn skal kaste exception :)
		}
	}
}
