package no.nav.poao_tilgang.application.client.axsys

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AxsysClientConfig(
	@Value("\${axsys.url}") val axsysUrl: String,
	@Value("\${axsys.scope}") val axsysScope: String,
) {

	@Bean
	open fun axsysClient(machineToMachineTokenClient: MachineToMachineTokenClient): AxsysClient {
		val client = AxsysClientImpl(
			baseUrl = axsysUrl,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(axsysScope) }
		)

		return CachedAxsysClient(client)
	}
}
