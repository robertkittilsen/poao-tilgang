package no.nav.poao_tilgang.core.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.slf4j.MDCContext
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.PolicyInput
import org.slf4j.LoggerFactory

object AbacDecisionDiff {

	private val log = LoggerFactory.getLogger(javaClass)

	private val secureLog = LoggerFactory.getLogger("SecureLog")

	fun toAbacDecision(harTilgangAbac: Boolean): Decision {
		return if (harTilgangAbac) Decision.Permit else Decision.Deny(
			"Deny fra ABAC",
			DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

	@OptIn(DelicateCoroutinesApi::class)
	fun <I : PolicyInput> asyncLogDecisionDiff(policyName: String, input: I, poaoTilgangPolicy: (input: I) -> Decision, abacPolicy: (input: I) ->  Decision) {
		GlobalScope.launch(MDCContext()) {
			try {
				val poaoTilgangDecision = poaoTilgangPolicy.invoke(input)
				val abacDecision = abacPolicy.invoke(input)

				if (abacDecision.type != poaoTilgangDecision.type) {
					secureLog.warn("Decision diff for policy $policyName - ulikt svar: ABAC=($abacDecision) POAO-tilgang=($poaoTilgangDecision) Input=$input")
				} else {
					secureLog.debug("Decision diff for policy $policyName - likt svar: ABAC=($abacDecision) POAO-tilgang=($poaoTilgangDecision) Input=$input")
				}
			} catch (e: Throwable) {
				log.error("Feil i POAO-tilgang implementasjon", e)
			}
		}
	}

}
