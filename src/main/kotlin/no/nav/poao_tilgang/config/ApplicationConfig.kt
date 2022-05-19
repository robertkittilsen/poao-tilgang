package no.nav.poao_tilgang.config

import no.nav.common.log.LogFilter
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableJwtTokenValidation
class ApplicationConfig {

	@Profile("default")
	@Bean
	fun machineToMachineTokenClient(): MachineToMachineTokenClient {
		return AzureAdTokenClientBuilder.builder()
			.withNaisDefaults()
			.buildMachineToMachineTokenClient()
	}

	@Bean
	fun logFilterRegistrationBean(): FilterRegistrationBean<LogFilter> {
		val registration = FilterRegistrationBean<LogFilter>()
		registration.filter = LogFilter(
			"poao-tilgang", EnvironmentUtils.isDevelopment().orElse(false)
		)
		registration.order = 1
		registration.addUrlPatterns("/*")
		return registration
	}

}
