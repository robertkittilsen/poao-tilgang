package no.nav.poao_tilgang.application.config

import no.nav.poao_tilgang.core.policy.ModiaPolicy
import no.nav.poao_tilgang.core.policy.impl.ModiaPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PolicyConfig {

	@Bean
	open fun modiaPolicy(adGruppeProvider: AdGruppeProvider): ModiaPolicy {
		return ModiaPolicyImpl(adGruppeProvider)
	}

}
