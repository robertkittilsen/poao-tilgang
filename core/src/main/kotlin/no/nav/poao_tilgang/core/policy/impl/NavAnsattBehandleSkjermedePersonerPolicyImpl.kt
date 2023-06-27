package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleSkjermedePersonerPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.hasAtLeastOne

class NavAnsattBehandleSkjermedePersonerPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleSkjermedePersonerPolicy {

	override val name = "NavAnsattBehandleSkjermedePersoner"

	private val behandleSkjermedePersonerGrupper = adGruppeProvider.hentTilgjengeligeAdGrupper().let {
		listOf(
			it.egneAnsatte
		)
	}

	override fun evaluate(input: NavAnsattBehandleSkjermedePersonerPolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.hasAtLeastOne(behandleSkjermedePersonerGrupper)
	}

}
