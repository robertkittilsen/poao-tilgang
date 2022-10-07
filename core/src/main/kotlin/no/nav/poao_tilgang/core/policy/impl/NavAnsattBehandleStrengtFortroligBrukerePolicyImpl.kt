package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligBrukerePolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.has

class NavAnsattBehandleStrengtFortroligBrukerePolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleStrengtFortroligBrukerePolicy {

	override val name = "NavAnsattBehandleStrengtFortroligBrukere"

	private val strengtFortroligAdresseAdGruppe = adGruppeProvider.hentTilgjengeligeAdGrupper().strengtFortroligAdresse

	override fun evaluate(input: NavAnsattBehandleStrengtFortroligBrukerePolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navIdent)
			.has(strengtFortroligAdresseAdGruppe)
	}

}
