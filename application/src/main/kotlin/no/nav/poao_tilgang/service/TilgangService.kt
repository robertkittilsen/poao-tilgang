package no.nav.poao_tilgang.service

import no.nav.common.log.MDCConstants
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.policy.ModiaPolicy
import no.nav.poao_tilgang.utils.SecureLog.secureLog
import org.slf4j.MDC
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

		secureLogPolicyResult(policyName, policyInput, decision)

		return decision
	}

	private fun <I> secureLogPolicyResult(policyName: String, input: I, decision: Decision) {
		val logLine = listOfNotNull(
			logValueWithDescription(policyName, "Policy result name"),
			logValueWithDescription(input, "input"),
			logValueWithDescription(decision.type, "decision"),
			logValueWithDescription(if (decision is Decision.Deny) decision.message else null, "denyMessage"),
			logValueWithDescription(if (decision is Decision.Deny) decision.reason else null, "denyReason"),
			logValueWithDescription(MDC.get(MDCConstants.MDC_CALL_ID), "callId"),
			logValueWithDescription(MDC.get(MDCConstants.MDC_USER_ID), "userId"),
			logValueWithDescription(MDC.get(MDCConstants.MDC_CONSUMER_ID), "consumerId"),
			logValueWithDescription(MDC.get(MDCConstants.MDC_REQUEST_ID), "requestId")
		).joinToString(" ")

		secureLog.info(logLine)
	}

	private fun <T> logValueWithDescription(value: T?, description: String): String? {
		return value?.let { "$description=$it" }
	}

}
