package no.nav.poao_tilgang.application.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.PolicyEvaluationRequestDto
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.EksternBrukerPolicyInputDto
import no.nav.poao_tilgang.api.dto.request.policy_input.ModiaPolicyInputDto
import no.nav.poao_tilgang.api.dto.request.policy_input.SkjermetPersonPolicyInputDto
import no.nav.poao_tilgang.api.dto.request.policy_input.StrengtFortroligBrukerPolicyInputDto
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.application.client.ClientObjectMapper.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class PoaoTilgangHttpClient(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient()
) : PoaoTilgangClient {

	override fun evaluatePolicy(input: PolicyInput): Decision {
		val request = PolicyRequest(
			requestId = UUID.randomUUID(),
			policyInput = input
		)

		val result = evaluatePolicies(listOf(request)).firstOrNull()
			?: throw IllegalStateException("Mangler result for policy evaluation request")

		return result.decision
	}

	override fun evaluatePolicies(requests: List<PolicyRequest>): List<PolicyResult> {
		return sendPolicyRequests(requests)
			.map { PolicyResult(it.requestId, it.decision.toDecision()) }
	}

	private fun sendPolicyRequests(requests: List<PolicyRequest>): List<PolicyEvaluationResultDto> {
		val requestDtos = requests.map { toRequestDto(it) }

		val requestJson = objectMapper.writeValueAsString(EvaluatePoliciesRequest(requestDtos))

		val request = Request.Builder()
			.url(joinPaths(baseUrl, "/api/v1/policy/evaluate"))
			.post(requestJson.toRequestBody("application/json".toMediaType()))
			.header("Authorization", "Bearer ${tokenProvider()}")
			.build()

		return client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Received bad status ${response.code}")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			objectMapper.readValue<EvaluatePoliciesResponse>(body).results
		}
	}

	private fun DecisionDto.toDecision(): Decision {
		return when (this.type) {
			DecisionType.PERMIT -> Decision.Permit
			DecisionType.DENY -> {
				val message = this.message
				val reason = this.reason

				check(message != null) { "message cannot be null" }
				check(reason != null) { "reason cannot be null" }

				Decision.Deny(message, reason)
			}
		}
	}

	private fun toRequestDto(policyRequest: PolicyRequest): PolicyEvaluationRequestDto<Any> {
		return when (policyRequest.policyInput) {
			is EksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = EksternBrukerPolicyInputDto(
					policyRequest.policyInput.navIdent,
					policyRequest.policyInput.norskIdent
				),
				policyId = PolicyId.EKSTERN_BRUKER_V1
			)
			is FortroligBrukerPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = FortroligBrukerPolicyInput(
					policyRequest.policyInput.navIdent,
				),
				policyId = PolicyId.FORTROLIG_BRUKER_V1
			)
			is ModiaPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = ModiaPolicyInputDto(
					policyRequest.policyInput.navIdent,
				),
				policyId = PolicyId.MODIA_V1
			)
			is SkjermetPersonPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = SkjermetPersonPolicyInputDto(
					policyRequest.policyInput.navIdent,
				),
				policyId = PolicyId.SKJERMET_PERSON_V1
			)
			is StrengtFortroligBrukerPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = StrengtFortroligBrukerPolicyInputDto(
					policyRequest.policyInput.navIdent,
				),
				policyId = PolicyId.STRENGT_FORTROLIG_BRUKER_V1
			)
		}
	}
}
