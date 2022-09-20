package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider

class NavAnsattTilgangTilNavEnhetPolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilNavEnhetPolicy {

	override val name = "NavAnsattTilgangTilNavEnhet"

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val erModiaAdmin = adGruppeProvider.hentAdGrupper(input.navIdent)
			.any { it.name == AdGrupper.MODIA_ADMIN }

		if (erModiaAdmin)
			return Decision.Permit

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(input.navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) Decision.Permit
		else Decision.Deny(
			"NAV ansatt har ikke tilgang til enheten ${input.navEnhetId}",
			DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
		)
	}

}
