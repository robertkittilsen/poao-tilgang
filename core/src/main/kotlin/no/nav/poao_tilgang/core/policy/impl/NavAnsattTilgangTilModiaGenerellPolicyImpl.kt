package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaGenerellPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.hasAtLeastOne

class NavAnsattTilgangTilModiaGenerellPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilModiaGenerellPolicy {

	private val tilgangTilModiaGrupper = adGruppeProvider.hentTilgjengeligeAdGrupper().let {
		listOf(
			it.modiaGenerell,
			it.modiaOppfolging
		)
	}

	override val name = "NavAnsattTilgangTilModiaGenerellPolicy"

	override fun evaluate(input: NavAnsattTilgangTilModiaGenerellPolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.hasAtLeastOne(tilgangTilModiaGrupper)
	}
}
