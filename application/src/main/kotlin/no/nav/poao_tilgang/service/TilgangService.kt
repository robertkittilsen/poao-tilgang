package no.nav.poao_tilgang.service

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.policy.ModiaPolicy
import no.nav.poao_tilgang.utils.SecureLog.secureLog
import org.springframework.stereotype.Service

@Service
class TilgangService(
	private val modiaPolicy: ModiaPolicy
) {

	fun harTilgangTilModia(navIdent: NavIdent): Decision {
		return executePolicy("HarTilgangTilModia", navIdent, modiaPolicy::harTilgang)
	}

	private fun <I> executePolicy(policyName: String, policyInput: I, policy: (input: I) -> Decision): Decision {
		val decision = policy.invoke(policyInput)

		secureLog.info("Policy result name=$policyName input=$policyInput decision=$decision")

		return decision
	}

}
