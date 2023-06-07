package no.nav.poao_tilgang.api_core_mapper

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.*
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class ApiCoreMapper(private val adGruppeProvider: AdGruppeProvider) {

	fun <T>mapToPolicyInput(policyId: PolicyId<T>, policyInput:T): PolicyInput {

		return when (policyId) {
			PolicyId.NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1 -> {
				val dto = policyInput as NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto

				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent),
					norskIdent = dto.norskIdent,
					tilgangType = TilgangType.SKRIVE
				)
			}
			PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2 -> {
				val dto = policyInput as NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto

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
				val dto =  policyInput as NavAnsattTilgangTilModiaPolicyInputV1Dto
				NavAnsattTilgangTilModiaPolicy.Input(dto.navAnsattAzureId)
			}

			PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1 -> {
				val dto = policyInput as EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto
				EksternBrukerTilgangTilEksternBrukerPolicy.Input(
					rekvirentNorskIdent = dto.rekvirentNorskIdent,
					ressursNorskIdent = dto.ressursNorskIdent
				)
			}

			PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1 -> {
				val dto = policyInput as NavAnsattTilgangTilNavEnhetPolicyInputV1Dto
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navEnhetId = dto.navEnhetId,
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}

			PolicyId.NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1 -> {
				val dto = policyInput as NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}

			PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1 -> {
				val dto = policyInput as NavAnsattBehandleFortroligBrukerePolicyInputV1Dto
				NavAnsattBehandleFortroligBrukerePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
			PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1 -> {
				val dto = policyInput as NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto
				NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId,
					navEnhetId = dto.navEnhetId,
				)
			}
			PolicyId.NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1 -> {
				val dto = policyInput as NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto
				NavAnsattBehandleSkjermedePersonerPolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
		}
	}

	fun toDecisionDto(decision: Decision): DecisionDto {
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
