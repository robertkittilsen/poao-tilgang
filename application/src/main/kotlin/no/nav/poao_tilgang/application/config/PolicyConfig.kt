package no.nav.poao_tilgang.application.config

import io.micrometer.core.instrument.MeterRegistry
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.policy.impl.*
import no.nav.poao_tilgang.core.provider.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PolicyConfig {

	@Bean
	open fun navAnsattTilgangTilEksternBrukerPolicy(
		abacProvider: AbacProvider,
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
		navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
		navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
		navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
		adGruppeProvider: AdGruppeProvider,
		meterRegistry: MeterRegistry,
		toggleProvider: ToggleProvider,
	): NavAnsattTilgangTilEksternBrukerPolicy {
		return NavAnsattTilgangTilEksternBrukerPolicyImpl(
			abacProvider,
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy,
			navAnsattTilgangTilSkjermetPersonPolicy,
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy,
			navAnsattTilgangTilOppfolgingPolicy,
			navAnsattTilgangTilModiaGenerellPolicy,
			adGruppeProvider,
			meterRegistry,
			toggleProvider
		)
	}

	@Bean
	open fun navAnsattTilgangTilAdressebeskyttetBrukerPolicy(
		diskresjonskodeProvider: DiskresjonskodeProvider,
		navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligUtlandBrukerePolicy: NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
	): NavAnsattTilgangTilAdressebeskyttetBrukerPolicy {
		return NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl(
			diskresjonskodeProvider,
			navAnsattBehandleFortroligBrukerePolicy,
			navAnsattBehandleStrengtFortroligBrukerePolicy,
			navAnsattBehandleStrengtFortroligUtlandBrukerePolicy
		)
	}

	@Bean
	open fun navAnsattTilgangTilNavEnhetMedSperrePolicy(
		navEnhetTilgangProvider: NavEnhetTilgangProvider,
		adGruppeProvider: AdGruppeProvider,
		abacProvider: AbacProvider,
		meterRegistry: MeterRegistry,
		toggleProvider: ToggleProvider,
		): NavAnsattTilgangTilNavEnhetMedSperrePolicy {
		return NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(
			navEnhetTilgangProvider,
			adGruppeProvider,
			abacProvider,
			meterRegistry,
			toggleProvider
		)
	}

	@Bean
	open fun navAnsattTilgangTilSkjermetPersonPolicy(
		skjermetPersonProvider: SkjermetPersonProvider,
		navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy
	): NavAnsattTilgangTilSkjermetPersonPolicy {
		return NavAnsattTilgangTilSkjermetPersonPolicyImpl(
			skjermetPersonProvider, navAnsattBehandleSkjermedePersonerPolicy
		)
	}

	@Bean
	open fun navAnsattTilgangTilEksternBrukerNavEnhetPolicy(
		oppfolgingsenhetProvider: OppfolgingsenhetProvider,
		geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider,
		adGruppeProvider: AdGruppeProvider,
		navEnhetTilgangProvider: NavEnhetTilgangProvider
	): NavAnsattTilgangTilEksternBrukerNavEnhetPolicy {
		return NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
			oppfolgingsenhetProvider,
			geografiskTilknyttetEnhetProvider,
			adGruppeProvider,
			navEnhetTilgangProvider
		)
	}

	@Bean
	open fun tilgangTilNavEnhetPolicy(
		navEnhetTilgangProvider: NavEnhetTilgangProvider,
		adGruppeProvider: AdGruppeProvider,
		abacProvider: AbacProvider,
		meterRegistry: MeterRegistry,
		toggleProvider: ToggleProvider,
		): NavAnsattTilgangTilNavEnhetPolicy {
		return NavAnsattTilgangTilNavEnhetPolicyImpl(
			navEnhetTilgangProvider, adGruppeProvider, abacProvider,meterRegistry,
			toggleProvider
		)
	}

	@Bean
	open fun eksternBrukerTilgangTilEksternBrukerPolicy(): EksternBrukerTilgangTilEksternBrukerPolicy {
		return EksternBrukerTilgangTilEksternBrukerPolicyImpl()
	}

	@Bean
	open fun navAnsattBehandleStrengtFortroligUtlandBrukerePolicy(
		adGruppeProvider: AdGruppeProvider
	): NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy {
		return NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl(adGruppeProvider)
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

	@Bean
	open fun navAnsattTilgangTilModiaGenerellPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilModiaGenerellPolicy {
		return NavAnsattTilgangTilModiaGenerellPolicyImpl(adGruppeProvider)
	}
}
