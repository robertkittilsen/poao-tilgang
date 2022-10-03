package no.nav.poao_tilgang.client

import no.nav.poao_tilgang.client.api.ApiResult
import java.util.*

interface PoaoTilgangClient {

	/**
	 * Evaluer en policy med gitt input, sjekk {@link no.nav.poao_tilgang.client.PolicyInput.kt} for hvilke policies som er tilgjengelig
	 */
	fun evaluatePolicy(input: PolicyInput): ApiResult<Decision>

	/**
	 * Evaluer flere policies, sjekk {@link no.nav.poao_tilgang.client.PolicyInput.kt} for hvilke policies som er tilgjengelig
	 */
	fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>>

	/**
	 * Henter alle Azure AD grupper til en NAV Ansatt ved bruk av objekt IDen til den ansatte
	 */
	fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>>

	/**
	 * Henter om en enkelt person er skjermet. Skjermet person var tidligere kjent som "egen ansatt"
	 */
	fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean>

	/**
	 * Henter om flere personer er skjermet. Skjermet person var tidligere kjent som "egen ansatt"
	 */
	fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>>

}

typealias NorskIdent = String

data class PolicyRequest(
	val requestId: UUID, // Unique pr PolicyRequest, used to find the matching PolicyResult
	val policyInput: PolicyInput
)

data class PolicyResult(
	val requestId: UUID, // Set to the value of requestId from PolicyRequest
	val decision: Decision
)

data class AdGruppe(
	val id: UUID,
	val navn: String // Ex: 0000-ga-123
)
