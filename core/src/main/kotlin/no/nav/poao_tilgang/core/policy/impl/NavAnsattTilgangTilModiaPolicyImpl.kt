package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.hasAtLeastOne

class NavAnsattTilgangTilModiaPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilModiaPolicy {

	private val tilgangTilModiaGrupper = adGruppeProvider.hentTilgjengeligeAdGrupper().let {
		listOf(
			it.modiaGenerell,
			it.modiaOppfolging,
			it.syfoSensitiv
		)
	}

	override val name = "NavAnsattTilgangTilModiaPolicy"
	override fun evaluate(input: NavAnsattTilgangTilModiaPolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navIdent)
			.hasAtLeastOne(tilgangTilModiaGrupper)
	}
}
