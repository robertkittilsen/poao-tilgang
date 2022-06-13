package no.nav.poao_tilgang.config

import no.nav.poao_tilgang.core.policy.ModiaPolicy
import no.nav.poao_tilgang.core.policy.impl.ModiaPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PolicyConfig {

	@Bean
	fun modiaPolicy(adGruppeProvider: AdGruppeProvider): ModiaPolicy {
		return ModiaPolicyImpl(adGruppeProvider)
	}

}
