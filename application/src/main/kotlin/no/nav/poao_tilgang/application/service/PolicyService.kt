package no.nav.poao_tilgang.application.service

import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.domain.PolicyEvaluationResult
import no.nav.poao_tilgang.application.utils.SecureLog
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.policy.impl.PolicyResolver
import no.nav.poao_tilgang.core.domain.PolicyResult
import org.springframework.stereotype.Service
import java.util.*
import kotlin.system.measureTimeMillis


@Service
class PolicyService(
	private val policyResolver: PolicyResolver
) {

	fun evaluatePolicyRequest(request: PolicyEvaluationRequest): PolicyEvaluationResult {

		var policyResult: PolicyResult

		val time = measureTimeMillis { policyResult = policyResolver.evaluate(request.input) }

		secureLogPolicyResult(
			requestId = request.requestId,
			policyInput = request.input,
			policyResult = policyResult,
			timeTakenMs = time
		)

		return PolicyEvaluationResult(request.requestId, policyResult.decision)
	}

	private fun secureLogPolicyResult(
		requestId: UUID,
		policyInput: PolicyInput,
		policyResult: PolicyResult,
		timeTakenMs: Long
	) {
		val (policyName, decision) = policyResult

		val policyResultInfo = listOfNotNull(
			logLabel("policy", policyName),
			logLabel("input", policyInput),
			logLabel("decision", decision.type),
			logLabel("timeTakenMs", timeTakenMs),
			logLabel("requestId", requestId),
			logLabel("denyMessage", if (decision is Decision.Deny) decision.message else null),
			logLabel("denyReason", if (decision is Decision.Deny) decision.reason else null),
		).joinToString(" ")

		SecureLog.secureLog.info("Policy result: $policyResultInfo")
	}

	private fun logLabel(label: String, value: Any?): String? {
		return value?.let { "$label=$it" }
	}

}

