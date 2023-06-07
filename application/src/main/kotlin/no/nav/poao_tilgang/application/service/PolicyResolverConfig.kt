package no.nav.poao_tilgang.application.service

import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.policy.impl.PolicyResolver
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PolicyResolverConfig {

	@Bean
	open fun policyResolver(
		navAnsattTilgangTilEksternBrukerPolicy: NavAnsattTilgangTilEksternBrukerPolicy,
		navAnsattTilgangTilModiaPolicy: NavAnsattTilgangTilModiaPolicy,
		eksternBrukerTilgangTilEksternBrukerPolicy: EksternBrukerTilgangTilEksternBrukerPolicy,
		navAnsattTilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicy,
		navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
		navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
		navAnsattTiltangTilEnhetMedSperrePolicy: NavAnsattTilgangTilNavEnhetMedSperrePolicy,
		navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy
	): PolicyResolver {
		return PolicyResolver(
			navAnsattTilgangTilEksternBrukerPolicy,
				navAnsattTilgangTilModiaPolicy,
				eksternBrukerTilgangTilEksternBrukerPolicy,
				navAnsattTilgangTilNavEnhetPolicy,
				navAnsattBehandleStrengtFortroligBrukerePolicy,
				navAnsattBehandleFortroligBrukerePolicy,
				navAnsattTiltangTilEnhetMedSperrePolicy,
				navAnsattBehandleSkjermedePersonerPolicy
		)
	}

	@Bean
	open fun apiCoreMapper(adGruppeProvider: AdGruppeProvider): ApiCoreMapper {
		return ApiCoreMapper(adGruppeProvider)
	}
}
