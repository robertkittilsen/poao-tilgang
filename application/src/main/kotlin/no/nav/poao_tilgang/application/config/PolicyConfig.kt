package no.nav.poao_tilgang.application.config

import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.policy.impl.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PolicyConfig {

	@Bean
	open fun eksternBrukerPolicy(abacProvider: AbacProvider): EksternBrukerPolicy {
		return EksternBrukerPolicyImpl(abacProvider)
	}

	@Bean
	open fun fortroligBrukerPolicy(adGruppeProvider: AdGruppeProvider): FortroligBrukerPolicy {
		return FortroligBrukerPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun modiaPolicy(adGruppeProvider: AdGruppeProvider): ModiaPolicy {
		return ModiaPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun skjermetPersonPolicy(adGruppeProvider: AdGruppeProvider): SkjermetPersonPolicy {
		return SkjermetPersonPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun strengtFortroligBrukerPolicy(adGruppeProvider: AdGruppeProvider): StrengtFortroligBrukerPolicy {
		return StrengtFortroligBrukerPolicyImpl(adGruppeProvider)
	}

}
