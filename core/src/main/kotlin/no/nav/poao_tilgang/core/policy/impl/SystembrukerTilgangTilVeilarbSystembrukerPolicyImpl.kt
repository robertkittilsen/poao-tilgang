package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.SystembrukerTilgangTilVeilarbSystembrukerPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.has

class SystembrukerTilgangTilVeilarbSystembrukerPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : SystembrukerTilgangTilVeilarbSystembrukerPolicy {

	private val tilgangTilVeilarbSystembruker = adGruppeProvider.hentTilgjengeligeAdGrupper().veilarbSystembruker

	override val name = "SystembrukerTilgangTilVeilarbSystembrukerPolicy"
	override fun evaluate(input: SystembrukerTilgangTilVeilarbSystembrukerPolicy.Input): Decision {
		val navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(input.systemressurs)
		return adGruppeProvider.hentAdGrupper(navAnsattAzureId)
			.has(tilgangTilVeilarbSystembruker)
	}
}
