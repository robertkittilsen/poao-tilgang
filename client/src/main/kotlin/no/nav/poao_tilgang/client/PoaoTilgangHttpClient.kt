package no.nav.poao_tilgang.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.api.dto.request.*
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilEksternBrukerPolicyInputDto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaPolicyInputDto
import no.nav.poao_tilgang.api.dto.response.*
import no.nav.poao_tilgang.client.ClientObjectMapper.objectMapper
import no.nav.poao_tilgang.client.api.*
import no.nav.poao_tilgang.client.api.ApiResult.Companion.failure
import no.nav.poao_tilgang.client.api.ApiResult.Companion.success
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

	private val jsonMediaType = "application/json".toMediaType()

	override fun evaluatePolicy(input: PolicyInput): ApiResult<Decision> {
		val request = PolicyRequest(
			requestId = UUID.randomUUID(),
			policyInput = input
		)

		return evaluatePolicies(listOf(request))
			.flatMap { data ->
				data.firstOrNull()?.decision?.let { success(it) }
					?: failure(ResponseDataApiException("Mangler result for policy evaluation request"))
			}
	}

	override fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>> {
		return sendPolicyRequests(requests)
			.map { data -> data.map { PolicyResult(it.requestId, it.decision.toDecision()) } }
	}

	override fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>> {
		val requestJson = objectMapper.writeValueAsString(HentAdGrupperForBrukerRequest(navAnsattAzureId))

		return sendRequest(
			path = "/api/v1/ad-gruppe",
			body = requestJson
		).map { body ->
			objectMapper.readValue<HentAdGrupperForBrukerResponse>(body)
				.map { AdGruppe(it.id, it.name) }
		}
	}

	override fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean> {
		return erSkjermetPerson(listOf(norskIdent))
			.flatMap { data ->
				data[norskIdent]?.let { success(it) }
					?: failure(ResponseDataApiException("Mangler data om skjermet person"))
			}
	}

	override fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>> {
		val requestJson = objectMapper.writeValueAsString(ErSkjermetPersonBulkRequest(norskeIdenter))

		return sendRequest(
			path = "/api/v1/skjermet-person",
			body = requestJson
		).map { objectMapper.readValue<ErSkjermetPersonBulkResponse>(it) }
	}

	private fun sendPolicyRequests(requests: List<PolicyRequest>): ApiResult<List<PolicyEvaluationResultDto>> {
		val requestDtos = requests.map { toRequestDto(it) }
		val requestJson = objectMapper.writeValueAsString(EvaluatePoliciesRequest(requestDtos))

		return sendRequest(
			path = "/api/v1/policy/evaluate",
			body = requestJson
		).map { objectMapper.readValue<EvaluatePoliciesResponse>(it).results }
	}

	//TODO Legg til håndtering av malformedBody (JSON feil)
	private fun sendRequest(
		path: String,
		method: String = "POST",
		body: String? = null,
	): ApiResult<String> {
		val request = Request.Builder()
			.url(joinPaths(baseUrl, path))
			.method(method, body?.toRequestBody(jsonMediaType))
			.header("Authorization", "Bearer ${tokenProvider()}")
			.build()

		return try {
			client.newCall(request).execute().use { response ->
				if (!response.isSuccessful) {
					return@use failure(BadHttpStatusApiException(response.code, response.body?.string()))
				}

				response.body?.string()?.let { success(it) }
					?: failure(MalformedResponseApiException.missingBody())
			}
		} catch (e: Exception) {
			failure(NetworkApiException(e))
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
			is NavAnsattTilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattTilgangTilEksternBrukerPolicyInputDto(
					policyRequest.policyInput.navIdent,
					policyRequest.policyInput.norskIdent
				),
				policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V1
			)
			is NavAnsattTilgangTilModiaPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattTilgangTilModiaPolicyInputDto(
					policyRequest.policyInput.navIdent,
				),
				policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1
			)
		}
	}
}
