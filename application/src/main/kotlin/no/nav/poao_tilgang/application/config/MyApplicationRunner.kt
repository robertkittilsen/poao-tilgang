package no.nav.poao_tilgang.application.config

import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MyApplicationRunner(private val microsoftClient: MicrosoftGraphClient): ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		try {
			microsoftClient.hentNavIdentMedAzureId(UUID.randomUUID())
		} catch (e: Exception) {
			//denn skal kaste exception :)
		}
	}
}
