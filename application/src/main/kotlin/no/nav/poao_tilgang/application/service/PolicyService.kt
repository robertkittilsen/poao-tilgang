package no.nav.poao_tilgang.application.service

import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.domain.PolicyEvaluationResult
import no.nav.poao_tilgang.application.exception.PolicyNotImplementedException
import no.nav.poao_tilgang.application.utils.SecureLog
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilSkjermetPersonPolicyImpl
import org.springframework.stereotype.Service
import java.util.*
import kotlin.system.measureTimeMillis

@Service
class PolicyService(
	private val navAnsattTilgangTilEksternBrukerPolicy: NavAnsattTilgangTilEksternBrukerPolicy,
	private val navAnsattTilgangTilModiaPolicy: NavAnsattTilgangTilModiaPolicy,
	private val eksternBrukerTilgangTilEksternBrukerPolicy: EksternBrukerTilgangTilEksternBrukerPolicy,
	private val navAnsattTilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicy,
	private val navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
	private val navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
	private val navAnsattTiltangTilEnhetMedSperrePolicy: NavAnsattTilgangTilNavEnhetMedSperrePolicy,
	private val navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy

) {

	fun evaluatePolicyRequest(request: PolicyEvaluationRequest): PolicyEvaluationResult {
		val policyResult = evaluate(request.input)

		secureLogPolicyResult(
			requestId = request.requestId,
			policyInput = request.input,
			policyResult = policyResult
		)

		return PolicyEvaluationResult(request.requestId, policyResult.decision)
	}

	private fun secureLogPolicyResult(
		requestId: UUID,
		policyInput: PolicyInput,
		policyResult: PolicyResult
	) {
		val (policyName, timeTakenMs, decision) = policyResult

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

	private fun evaluate(input: PolicyInput): PolicyResult {
		return when (input) {
			is NavAnsattTilgangTilEksternBrukerPolicy.Input -> evaluate(input, navAnsattTilgangTilEksternBrukerPolicy)
			is NavAnsattTilgangTilModiaPolicy.Input -> evaluate(input, navAnsattTilgangTilModiaPolicy)
			is EksternBrukerTilgangTilEksternBrukerPolicy.Input -> evaluate(input, eksternBrukerTilgangTilEksternBrukerPolicy)
			is NavAnsattTilgangTilNavEnhetPolicy.Input -> evaluate(input, navAnsattTilgangTilNavEnhetPolicy)
			is NavAnsattBehandleFortroligBrukerePolicy.Input -> evaluate(input, navAnsattBehandleFortroligBrukerePolicy)
			is NavAnsattBehandleStrengtFortroligBrukerePolicy.Input -> evaluate(input, navAnsattBehandleStrengtFortroligBrukerePolicy)
			is NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input -> evaluate(input, navAnsattTiltangTilEnhetMedSperrePolicy)
			is NavAnsattBehandleSkjermedePersonerPolicy.Input -> evaluate(input, navAnsattBehandleSkjermedePersonerPolicy)
			else -> throw PolicyNotImplementedException("Håndtering av policy ${input.javaClass.canonicalName} er ikke implementert")
		}
	}

	private fun <I : PolicyInput> evaluate(input: I, policy: Policy<I>): PolicyResult {
		var decision: Decision

		val time = measureTimeMillis { decision = policy.evaluate(input) }

		return PolicyResult(policy.name, time, decision)
	}

	private data class PolicyResult(
		val policyName: String,
		val timeTakenMs: Long,
		val decision: Decision
	)

}

