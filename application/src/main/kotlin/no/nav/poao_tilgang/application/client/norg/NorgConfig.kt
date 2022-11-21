package no.nav.poao_tilgang.application.client.norg

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class NorgConfig(
	@Value("\${norg.url}") val norgUrl: String
) {

	@Bean
	open fun norgClient(): NorgClient {
		return NorgCachedClient(NorgHttpClient(norgUrl))
	}
}
