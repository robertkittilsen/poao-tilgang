package no.nav.poao_tilgang.application.config

import no.nav.common.abac.*
import no.nav.common.abac.audit.AuditConfig
import no.nav.common.abac.audit.NimbusSubjectProvider
import no.nav.common.log.LogFilter
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.poao_tilgang.application.middleware.RequesterLogFilter
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableJwtTokenValidation
open class ApplicationConfig {

	companion object {
		const val APPLICATION_NAME = "poao-tilgang"
	}

	@Profile("default")
	@Bean
	open fun machineToMachineTokenClient(): MachineToMachineTokenClient {
		return AzureAdTokenClientBuilder.builder()
			.withNaisDefaults()
			.buildMachineToMachineTokenClient()
	}

	@Bean
	open fun logFilterRegistrationBean(): FilterRegistrationBean<LogFilter> {
		val registration = FilterRegistrationBean<LogFilter>()
		registration.filter = LogFilter(
			APPLICATION_NAME, EnvironmentUtils.isDevelopment().orElse(false)
		)
		registration.order = -1
		registration.addUrlPatterns("/*")
		return registration
	}

	@Bean
	open fun requesterLogFilterRegistrationBean(
		tokenValidationContextHolder: TokenValidationContextHolder
	): FilterRegistrationBean<RequesterLogFilter> {
		val registration = FilterRegistrationBean<RequesterLogFilter>()
		registration.filter = RequesterLogFilter(tokenValidationContextHolder)

		registration.order = 1
		registration.addUrlPatterns("/api/*")
		return registration
	}


	@Bean
	open fun abacClient(
		machineToMachineTokenClient: MachineToMachineTokenClient,
		@Value("\${abac.url}") abacUrl: String,
		@Value("\${abac.scope}") abacScope: String
	): AbacClient {
		val client = AbacHttpClient(abacUrl)
		{ "Bearer " + machineToMachineTokenClient.createMachineToMachineToken(abacScope) }

		return AbacCachedClient(client)
	}

	@Bean
	open fun pep(abacClient: AbacClient): Pep {
		return VeilarbPep(
			APPLICATION_NAME,
			abacClient,
			NimbusSubjectProvider(),
			AuditConfig(null, null, null)
		)
	}

}
