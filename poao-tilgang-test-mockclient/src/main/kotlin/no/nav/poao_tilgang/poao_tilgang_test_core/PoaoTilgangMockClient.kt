package no.nav.poao_tilgang.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.client.api.ApiResult
import no.nav.poao_tilgang.client.api.ResponseDataApiException
import no.nav.poao_tilgang.poao_tilgang_test_core.NavContext
import no.nav.poao_tilgang.poao_tilgang_test_core.Policies
import java.util.*

internal object ClientObjectMapper {
	val objectMapper: ObjectMapper = ObjectMapper()
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
class PoaoTilgangMockClient(val navContext: NavContext = NavContext()): PoaoTilgangClient {
	private val policyes = Policies(navContext)
	private val apiCoreMapper = ApiCoreMapper(policyes.providers.adGruppeProvider)
	private val resolver = policyes.policyResolver
	override fun evaluatePolicy(input: PolicyInput): ApiResult<Decision> {
		val request = PolicyRequest(
			requestId = UUID.randomUUID(),
			policyInput = input
		)

		val valuatePolicy = valuatePolicy(request)
		return ApiResult.success(valuatePolicy)
	}

	override fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>> {
		val desicions = requests.map{
			PolicyResult(it.requestId, valuatePolicy(it))
		}
		return ApiResult.success(desicions)
	}


	private fun valuatePolicy(input: PolicyRequest): Decision {
		val requestDto = toRequestDto(input)

		val valueToTree = ClientObjectMapper.objectMapper.valueToTree<JsonNode>(requestDto.policyInput)
		val policyInput = apiCoreMapper.mapToPolicyInput(requestDto.policyId, valueToTree)
		val result = resolver.evaluate(policyInput)
		val decision = result.decision
		return when(decision) {
			is no.nav.poao_tilgang.core.domain.Decision.Permit -> Decision.Permit
			is no.nav.poao_tilgang.core.domain.Decision.Deny -> Decision.Deny(decision.message, decision.reason.name)
		}

	}

	override fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>> {
		val adGrupper = policyes.providers.adGruppeProvider.hentAdGrupper(navAnsattAzureId)

		val map = adGrupper.map { adGruppe ->
			AdGruppe(adGruppe.id, adGruppe.navn)
		}
		return ApiResult.success(map)
	}

	override fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean> {
		val eksternBruker = navContext.privatBrukere.get(norskIdent) ?: return ApiResult.failure<Boolean>(
			ResponseDataApiException("Brukern finnes ikke")
		)

		return ApiResult.success(eksternBruker.erSkjermet)
	}

	override fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>> {
		val toMap = navContext.erSkjermetPerson(norskeIdenter)
		return ApiResult.success(toMap)
	}
}
