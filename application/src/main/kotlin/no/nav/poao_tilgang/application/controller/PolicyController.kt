package no.nav.poao_tilgang.application.controller

import com.fasterxml.jackson.databind.JsonNode
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.PolicyEvaluationRequestDto
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.*
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.service.PolicyService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonNode
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.policy.*
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/policy")
class PolicyController(
	private val authService: AuthService,
	private val policyService: PolicyService,
	private val apiCoreMapper: ApiCoreMapper
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/evaluate")
	fun evaluatePolicies(@RequestBody evaluatePoliciesRequest: EvaluatePoliciesRequest<JsonNode>): EvaluatePoliciesResponse {
		authService.verifyRequestIsMachineToMachine()

		val evaluations = evaluatePoliciesRequest.requests
			.map { evaluateRequest(it) }

		return EvaluatePoliciesResponse(evaluations)
	}

	private fun evaluateRequest(request: PolicyEvaluationRequestDto<JsonNode>): PolicyEvaluationResultDto {
		val policyInput = tulleMap(request.policyId, request.policyInput)

		val result = policyService.evaluatePolicyRequest(
			PolicyEvaluationRequest(request.requestId, policyInput)
		)

		return PolicyEvaluationResultDto(result.requestId, apiCoreMapper.toDecisionDto(result.decision))
	}

	private fun tulleMap(policyId: PolicyId<*>, policyInput: JsonNode): PolicyInput {
		return when (policyId) {
			PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1 -> mapToPolicyInput(PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1, policyInput)
			PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1, policyInput)
			PolicyId.NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1, policyInput)
			PolicyId.NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1, policyInput)
			PolicyId.NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1, policyInput)
			PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2 -> mapToPolicyInput(PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2, policyInput)
			PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1, policyInput)
			PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1, policyInput)
			PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1 -> mapToPolicyInput(PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1, policyInput)
		}
	}

	private inline fun <reified T> mapToPolicyInput(policyId: PolicyId<T>, policyInput: JsonNode): PolicyInput {
		val fromJsonNode = fromJsonNode<T>(policyInput)
		return apiCoreMapper.mapToPolicyInput(policyId, fromJsonNode)
	}



}

