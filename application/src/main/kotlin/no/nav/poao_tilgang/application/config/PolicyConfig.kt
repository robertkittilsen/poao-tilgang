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
	open fun navAnsattTilgangTilEksternBrukerPolicy(abacProvider: AbacProvider): NavAnsattTilgangTilEksternBrukerPolicy {
		return NavAnsattTilgangTilEksternBrukerPolicyImpl(abacProvider)
	}

	@Bean
	open fun navAnsattBehandleFortroligBrukerePolicy(adGruppeProvider: AdGruppeProvider): NavAnsattBehandleFortroligBrukerePolicy {
		return NavAnsattBehandleFortroligBrukerePolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattTilgangTilModiaPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilModiaPolicy {
		return NavAnsattTilgangTilModiaPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattBehandleSkjermedePersonerPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattBehandleSkjermedePersonerPolicy {
		return NavAnsattBehandleSkjermedePersonerPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattBehandleStrengtFortroligBrukerePolicy(adGruppeProvider: AdGruppeProvider): NavAnsattBehandleStrengtFortroligBrukerePolicy {
		return NavAnsattBehandleStrengtFortroligBrukerePolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattTilgangTilOppfolgingPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilOppfolgingPolicy {
		return NavAnsattTilgangTilOppfolgingPolicyImpl(adGruppeProvider)
	}

}
