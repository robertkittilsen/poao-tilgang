package no.nav.poao_tilgang.application.client.axsys

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AxsysClientConfig(
	@Value("\${axsys.scope}") val axsysScope: String,
	@Value("\${poao-gcp-proxy.url}") val proxyUrl: String,
	@Value("\${poao-gcp-proxy.scope}") val proxyScope: String,
) {

	@Bean
	open fun axsysClient(machineToMachineTokenClient: MachineToMachineTokenClient): AxsysClient {
		val delegate = AxsysClientImpl(
			baseUrl = proxyUrl,
			proxyTokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(proxyScope) },
			axsysTokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(axsysScope) },
		)

		return CachedAxsysClient(delegate)
	}

}
