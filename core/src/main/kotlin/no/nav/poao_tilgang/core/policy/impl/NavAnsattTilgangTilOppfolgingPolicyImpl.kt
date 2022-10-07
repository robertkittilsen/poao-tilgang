package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.has

class NavAnsattTilgangTilOppfolgingPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilOppfolgingPolicy {

	private val modiaOppfolging = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging

	override val name = "NavAnsattTilgangTilOppfolgingPolicy"

	override fun evaluate(input: NavAnsattTilgangTilOppfolgingPolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navIdent)
			.has(modiaOppfolging)
	}

}
