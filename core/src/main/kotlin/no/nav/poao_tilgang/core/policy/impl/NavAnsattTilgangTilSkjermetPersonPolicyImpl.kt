package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleSkjermedePersonerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilSkjermetPersonPolicy
import no.nav.poao_tilgang.core.provider.SkjermetPersonProvider

class NavAnsattTilgangTilSkjermetPersonPolicyImpl(
	private val skjermetPersonProvider: SkjermetPersonProvider,
	private val navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy
) : NavAnsattTilgangTilSkjermetPersonPolicy {

	override val name = "NavAnsattTilgangTilSkjermetPerson"

	override fun evaluate(input: NavAnsattTilgangTilSkjermetPersonPolicy.Input): Decision {
		val erSkjermet = skjermetPersonProvider.erSkjermetPerson(input.norskIdent)

		if (erSkjermet) {
			return navAnsattBehandleSkjermedePersonerPolicy.evaluate(
				NavAnsattBehandleSkjermedePersonerPolicy.Input(input.navIdent)
			)
		}

		return Decision.Permit
	}

}
