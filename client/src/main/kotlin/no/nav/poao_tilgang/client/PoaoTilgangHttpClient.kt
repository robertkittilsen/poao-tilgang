package no.nav.poao_tilgang.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.api.dto.request.*
import no.nav.poao_tilgang.api.dto.request.policy_input.*
import no.nav.poao_tilgang.api.dto.response.*
import no.nav.poao_tilgang.client.ClientObjectMapper.objectMapper
import no.nav.poao_tilgang.client.api.*
import no.nav.poao_tilgang.client.api.ApiResult.Companion.failure
import no.nav.poao_tilgang.client.api.ApiResult.Companion.success
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
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

		return sendRequest<HentAdGrupperForBrukerResponse>(
			path = "/api/v1/ad-gruppe", body = requestJson
		).map { adGrupper ->
			adGrupper.map { adGruppe ->
				AdGruppe(adGruppe.id, adGruppe.name)
			}
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
		)
	}

	private fun sendPolicyRequests(requests: List<PolicyRequest>): ApiResult<List<PolicyEvaluationResultDto>> {
		val requestDtos = requests.map { toRequestDto(it) }
		val requestJson = objectMapper.writeValueAsString(EvaluatePoliciesRequest(requestDtos))

		return sendRequest<EvaluatePoliciesResponse>(
			path = "/api/v1/policy/evaluate",
			body = requestJson
		).map { it.results }
	}

	private inline fun <reified D> sendRequest(
		path: String,
		method: String = "POST",
		body: String? = null,
	): ApiResult<D> {
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

				return response.body?.string()?.let { parseBody(it) }
					?: failure(ResponseDataApiException.missingBody())
			}
		} catch (e: Throwable) {
			when (e) {
				is IOException -> failure(NetworkApiException(e))
				else -> failure(UnspecifiedApiException(e))
			}
		}
	}

	private inline fun <reified D> parseBody(body: String): ApiResult<D> {
		return try {
			success(objectMapper.readValue(body))
		} catch (e: Throwable) {
			failure(ResponseDataApiException(e.message ?: "Unknown error"))
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
				policyInput = NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto(
					navAnsattAzureId = policyRequest.policyInput.navAnsattAzureId,
					norskIdent = policyRequest.policyInput.norskIdent,
					tilgangType = when(policyRequest.policyInput.tilgangType) {
						TilgangType.LESE -> no.nav.poao_tilgang.api.dto.request.TilgangType.LESE
						TilgangType.SKRIVE -> no.nav.poao_tilgang.api.dto.request.TilgangType.SKRIVE
					}
				),
				policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2
			)

			is NavAnsattTilgangTilModiaPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattTilgangTilModiaPolicyInputV1Dto(
					navAnsattAzureId = policyRequest.policyInput.navAnsattAzureId,
				),
				policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1
			)

			is EksternBrukerTilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto(
					rekvirentNorskIdent = policyRequest.policyInput.rekvirentNorskIdent,
					ressursNorskIdent = policyRequest.policyInput.ressursNorskIdent
				),
				policyId = PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1
			)

			is NavAnsattTilgangTilNavEnhetPolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattTilgangTilNavEnhetPolicyInputV1Dto(
					navAnsattAzureId = policyRequest.policyInput.navAnsattAzureId,
					navEnhetId = policyRequest.policyInput.navEnhetId
				),
				policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1
			)

			is NavAnsattBehandleStrengtFortroligBrukerePolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto(
					navAnsattAzureId = policyRequest.policyInput.navAnsattAzureId
				),
				policyId = PolicyId.NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1
			)

			is NavAnsattBehandleFortroligBrukerePolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattBehandleFortroligBrukerePolicyInputV1Dto(
					navAnsattAzureId = policyRequest.policyInput.navAnsattAzureId
				),
				policyId = PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1
			)

			is NavAnsattTilgangTilNavEnhetMedSperrePolicyInput -> PolicyEvaluationRequestDto(
				requestId = policyRequest.requestId,
				policyInput = NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto(
					navAnsattAzureId = policyRequest.policyInput.navAnsattAzureId,
					navEnhetId = policyRequest.policyInput.navEnhetId
				),
				policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1
			)
		}
	}
}
