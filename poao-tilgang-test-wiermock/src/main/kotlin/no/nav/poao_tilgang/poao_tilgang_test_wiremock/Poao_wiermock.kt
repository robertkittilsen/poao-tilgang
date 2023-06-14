package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import no.nav.poao_tilgang.api.dto.request.ErSkjermetPersonBulkRequest
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.api.dto.request.HentAdGrupperForBrukerRequest
import no.nav.poao_tilgang.api.dto.response.*
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaPolicy
import no.nav.poao_tilgang.poao_tilgang_test_core.Polecys
import kotlin.reflect.KFunction1

class Managed_wiermock(portnummer: Int = 0, baspath: String= "") {
	val mocks = Poao_wiermock(baspath = baspath)
	val wireMockServer = WireMockServer(
		wireMockConfig()
			.port(portnummer)
			.extensions(*mocks.listOfExtension)
	)

	val navModell = mocks.navModell

	init {
		wireMockServer.stubFor(
			WireMock.post(mocks.skjermetPerson.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.skjermetPerson.name)
			)
		)
		wireMockServer.stubFor(
			WireMock.post(mocks.adgroupController.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.adgroupController.name)
			)
		)
		wireMockServer.stubFor(
			WireMock.post(mocks.tilgangsKontroller.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.tilgangsKontroller.name)
			)
		)
		wireMockServer.stubFor(
			WireMock.post(mocks.polecyController.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.polecyController.name)
			)
		)

		wireMockServer.start()
	}
}
class Poao_wiermock(val polecys: Polecys = Polecys(), baspath : String) {
	val navModell = polecys.navModel


	val skjermetPerson = Response("skjermetPerson", "$baspath/api/v1/skjermet-person", ::kjermetPerson, ErSkjermetPersonBulkRequest::class.java)
	val adgroupController = Response("adgroupController", "$baspath/api/v1/ad-gruppe", ::getAdGropper, HentAdGrupperForBrukerRequest::class.java)
	val tilgangsKontroller = Response("tilgangsKontroller", "$baspath/api/v1/tilgang/modia", ::harTilgang, HarTilgangTilModiaRequest::class.java)
	val polecyController = PolicyController(polecys, baspath)

	val listOfExtension = arrayOf(skjermetPerson, adgroupController, polecyController, tilgangsKontroller)

	private fun kjermetPerson(model: ErSkjermetPersonBulkRequest): Map<NorskIdent, Boolean> {
		return navModell.erSkjermetPerson(model.norskeIdenter)
	}
	private fun harTilgang(harTilgangTilModiaRequest: HarTilgangTilModiaRequest): TilgangResponse {
		val navIdent = harTilgangTilModiaRequest.navIdent
		val navAnsatt = navModell.henNavAnsatt(navIdent)
			?: return TilgangResponse(DecisionDto(DecisionType.DENY, "ikke satt i mock", "Ikke ansatt"))


		val evaluate =  polecys.navAnsattTilgangTilModiaPolicy.evaluate(NavAnsattTilgangTilModiaPolicy.Input(navAnsatt.azureObjectId))
		val decisionDto = decisionDto(evaluate)
		return TilgangResponse(decisionDto)

	}
	private fun getAdGropper(model: HentAdGrupperForBrukerRequest): HentAdGrupperForBrukerResponse? {
		return navModell
			.henNavAnsatt(model.navAnsattAzureId)
			?.adGrupper
			?.map {
				AdGruppeDto(
					it.id,
					it.navn
				)
			}
	}
}



class PolicyController(val polecys: Polecys, baspath: String) : ResponseDefinitionTransformer() {
	val apiCoreMapper = ApiCoreMapper(polecys.providers.adGruppeProvider)

	val path = "$baspath/api/v1/policy/evaluate"
	override fun getName(): String {
		return "policyController"
	}

	override fun transform(
		request: Request,
		responseDefinition: ResponseDefinition,
		files: FileSource?,
		parameters: Parameters?
	): ResponseDefinition {
		val bodyAsString = request.bodyAsString
		val readValue = ClientObjectMapper.objectMapper.readValue(
			bodyAsString,
			object : TypeReference<EvaluatePoliciesRequest<JsonNode>>() {})

		val response = response(readValue)


		return ResponseDefinitionBuilder()
			.withHeader("Content-Type", "application/json")
			.withStatus(200)
			.withBody(ClientObjectMapper.objectMapper.writeValueAsString(response))
			.build()

	}

	private fun response(requestDto: EvaluatePoliciesRequest<JsonNode>): EvaluatePoliciesResponse {
		val a = requestDto.requests.map {
			val kake = apiCoreMapper.mapToPolicyInput(it.policyId, it.policyInput)
			val evaluate = polecys.policyResolver.evaluate(kake)
			val value = decisionDto(evaluate.decision)

			PolicyEvaluationResultDto(it.requestId,  value)
		}
		return EvaluatePoliciesResponse(a)
	}

	override fun applyGlobally(): Boolean {
		return false
	}
}

private fun decisionDto(decision: Decision): DecisionDto {
	return when (decision) {
		is Decision.Permit -> DecisionDto(
			DecisionType.PERMIT,
			null, null
		)

		is Decision.Deny -> DecisionDto(
			DecisionType.DENY,
			decision.message,
			decision.reason.name,
			)
	}
}

class Response<T>(
	private val name: String,
	val path: String,
	val responsFunc: KFunction1<T, Any?>,
	val requestBody: Class<T>
): ResponseDefinitionTransformer()   {
	override fun getName(): String {
		return name
	}

	override fun transform(
		request: Request,
		responseDefinition: ResponseDefinition,
		files: FileSource?,
		parameters: Parameters?
	): ResponseDefinition {

		val bodyAsString = request.bodyAsString
		val requestDto = ClientObjectMapper.objectMapper.readValue(bodyAsString, requestBody)
		val response = responsFunc(requestDto)

		return ResponseDefinitionBuilder()
			.withHeader("Content-Type", "application/json")
			.withStatus(200)
			.withBody(ClientObjectMapper.objectMapper.writeValueAsString(response))
			.build()

	}

	override fun applyGlobally(): Boolean {
		return false
	}

}

internal object ClientObjectMapper {
	val objectMapper: ObjectMapper = ObjectMapper()
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
