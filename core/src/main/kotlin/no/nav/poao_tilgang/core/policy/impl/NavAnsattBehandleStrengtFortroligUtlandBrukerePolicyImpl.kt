package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.has

class NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy {

	private val strengtFortroligAdresseAdGruppe = adGruppeProvider.hentTilgjengeligeAdGrupper().strengtFortroligAdresse

	override val name = "NavAnsattBehandleStrengtFortroligUtlandBrukere"

	override fun evaluate(input: NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navIdent)
			.has(strengtFortroligAdresseAdGruppe)
	}

}
