package no.nav.poao_tilgang.client.microsoft_graph

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MicrosoftGraphClientConfig {

	@Value("\${microsoft_graph.url}")
	lateinit var baseUrl: String

	@Value("\${microsoft_graph.scope}")
	lateinit var scope: String

	@Bean
	fun microsoftGraphClient(machineToMachineTokenClient: MachineToMachineTokenClient): MicrosoftGraphClient {
		return MicrosoftGraphClientImpl(
			baseUrl = baseUrl,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(scope) }
		)
	}

}
