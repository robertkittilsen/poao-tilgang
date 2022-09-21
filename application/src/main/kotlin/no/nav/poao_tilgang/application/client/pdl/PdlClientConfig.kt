package no.nav.poao_tilgang.application.client.pdl

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlClientConfig {

	@Value("\${pdl.url}")
	lateinit var url: String

	@Value("\${pdl.scope}")
	lateinit var scope: String

	@Bean
	open fun pdlClient(machineToMachineTokenClient: MachineToMachineTokenClient): PdlClient {
		val client = PdlClientImpl(
			baseUrl = url,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(scope) },
		)

		return CachedPdlClient(client)
	}

}
