package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.has

class NavAnsattBehandleFortroligBrukerePolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleFortroligBrukerePolicy {

	override val name = "NavAnsattBehandleFortroligBrukere"

	private val fortroligAdresseAdGruppe = adGruppeProvider.hentTilgjengeligeAdGrupper().fortroligAdresse

	override fun evaluate(input: NavAnsattBehandleFortroligBrukerePolicy.Input): Decision {
		return adGruppeProvider.hentAdGrupper(input.navIdent)
			.has(fortroligAdresseAdGruppe)
	}
}
