package no.nav.poao_tilgang.application.client.skjermet_person

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SkjermetPersonClientConfig {

	@Value("\${skjermet_person.url}")
	lateinit var baseUrl: String

	@Value("\${skjermet_person.scope}")
	lateinit var scope: String

	@Bean
	open fun skjermetPersonClient(machineToMachineTokenClient: MachineToMachineTokenClient): SkjermetPersonClient {
		return SkjermetPersonClientImpl(
			baseUrl = baseUrl,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(scope) }
		)
	}

}
