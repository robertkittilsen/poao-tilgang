package no.nav.poao_tilgang.application.client.norg

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class NorgConfig(
	@Value("\$runtimeLocation") val runtimeLocation: String,
	@Value("\${norg.url:#{null}}") val norgUrl: String?,
	@Value("\${poao-gcp-proxy.url:#{null}}") val proxyUrl: String?,
	@Value("\${poao-gcp-proxy.scope:#{null}}") val proxyScope: String?
) {

	@Bean
	open fun norgClient(machineToMachineTokenClient: MachineToMachineTokenClient): NorgClient {

		val client = if (runtimeLocation == "GCP") {
			check(proxyUrl != null) { "Missing required property proxyUrl" }
			check(proxyScope != null) { "Missing required property proxyScope" }

			NorgHttpClient(
				"$proxyUrl/proxy/norg2",
				{ machineToMachineTokenClient.createMachineToMachineToken(proxyScope) })
		} else {
			check(norgUrl != null) { "Missing required property norgUrl" }
			NorgHttpClient(norgUrl, null)
		}

		return NorgCachedClient(client)
	}
}
