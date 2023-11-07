package no.nav.poao_tilgang.api_core_mapper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.*
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class ApiCoreMapper(private val adGruppeProvider: AdGruppeProvider) {

	//eksisiterer også en instangs av objectmapper i application/utils/JsonUtils.kt
	private val objectMapper: ObjectMapper = ObjectMapper()
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


	private inline fun <reified T> fromJsonNode(jsonNode: JsonNode): T {
		return objectMapper.treeToValue(jsonNode)

	}
	fun mapToPolicyInput(policyId: PolicyId, policyInput: JsonNode): PolicyInput {
		return when (policyId) {
			PolicyId.NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1 -> {
				val dto = fromJsonNode<NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto>(policyInput)

				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent),
					norskIdent = dto.norskIdent,
					tilgangType = TilgangType.SKRIVE
				)
			}
			PolicyId.NAV_ANSATT_NAV_IDENT_LESETILGANG_TIL_EKSTERN_BRUKER_V1 -> {
				val dto = fromJsonNode<NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto>(policyInput)

				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent),
					norskIdent = dto.norskIdent,
					tilgangType = TilgangType.LESE
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

			PolicyId.NAV_ANSATT_NAV_IDENT_TILGANG_TIL_NAV_ENHET_V1 -> {
				val dto = fromJsonNode<NavAnsattNavIdentTilgangTilNavEnhetPolicyInputV1Dto>(policyInput)
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navEnhetId = dto.navEnhetId,
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent)
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
			PolicyId.NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1 -> {
				val dto = fromJsonNode<NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto>(policyInput)
				NavAnsattBehandleSkjermedePersonerPolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
		}
	}

}
