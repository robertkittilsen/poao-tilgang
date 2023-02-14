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
import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.service.PolicyService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonNode
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
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
	private val adGruppeProvider: AdGruppeProvider
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
		val policyInput = mapToPolicyInput(request.policyId, request.policyInput)

		val result = policyService.evaluatePolicyRequest(
			PolicyEvaluationRequest(request.requestId, policyInput)
		)

		return PolicyEvaluationResultDto(result.requestId, toDecisionDto(result.decision))
	}

	private fun mapToPolicyInput(policyId: PolicyId, policyInput: JsonNode): PolicyInput {
		return when (policyId) {
			PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1 -> {
				val dto = fromJsonNode<NavAnsattTilgangTilEksternBrukerPolicyInputV1Dto>(policyInput)

				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent),
					norskIdent = dto.norskIdent,
					tilgangType = TilgangType.SKRIVE
				)
			}
			PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2 -> {
				val dto = fromJsonNode<NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto>(policyInput)

				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId,
					norskIdent = dto.norskIdent,
					tilgangType = when (dto.tilgangType) {
						no.nav.poao_tilgang.api.dto.request.TilgangType.LESE -> TilgangType.LESE
						no.nav.poao_tilgang.api.dto.request.TilgangType.SKRIVE -> TilgangType.SKRIVE
					}
				)
			}
			PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1 -> {
				val dto =  fromJsonNode<NavAnsattTilgangTilModiaPolicyInputV1Dto>(policyInput)
				NavAnsattTilgangTilModiaPolicy.Input(dto.navAnsattAzureId)
			}

			PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1 -> {
				val dto = fromJsonNode<EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto>(policyInput)
				EksternBrukerTilgangTilEksternBrukerPolicy.Input(
					rekvirentNorskIdent = dto.rekvirentNorskIdent,
					ressursNorskIdent = dto.ressursNorskIdent
				)
			}

			PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1 -> {
				val dto = fromJsonNode<NavAnsattTilgangTilNavEnhetPolicyInputV1Dto>(policyInput)
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navEnhetId = dto.navEnhetId,
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}

			PolicyId.NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1 -> {
				val dto = fromJsonNode<NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto>(policyInput)
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}

			PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1 -> {
				val dto = fromJsonNode<NavAnsattBehandleFortroligBrukerePolicyInputV1Dto>(policyInput)
				NavAnsattBehandleFortroligBrukerePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
			PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1 -> {
				val dto = fromJsonNode<NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto>(policyInput)
				NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId,
					navEnhetId = dto.navEnhetId,
				)
			}
		}
	}

	private fun toDecisionDto(decision: Decision): DecisionDto {
		return when (decision) {
			is Decision.Permit -> DecisionDto(
				type = DecisionType.PERMIT,
				message = null,
				reason = null
			)
			is Decision.Deny -> DecisionDto(
				type = DecisionType.DENY,
				message = decision.message,
				reason = decision.reason.name
			)
		}
	}

}

