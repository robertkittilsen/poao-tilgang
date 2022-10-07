package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.utils.has

class NavAnsattTilgangTilNavEnhetPolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilNavEnhetPolicy {

	private val modiaAdmin = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaAdmin

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhet"

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val erModiaAdmin = adGruppeProvider.hentAdGrupper(input.navIdent)
			.has(modiaAdmin)
			.isPermit

		if (erModiaAdmin)
			return Decision.Permit

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(input.navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) Decision.Permit else denyDecision
	}

}
