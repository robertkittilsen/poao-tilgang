package no.nav.poao_tilgang.api.dto.request

import no.nav.poao_tilgang.api.dto.request.policy_input.*

sealed class PolicyId<T> {
	abstract val policyDtoClass: Class<T>

	object NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1 :
		PolicyId<NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto>
			get() = NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto::class.java
	}

	object NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2 :
		PolicyId<NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto>() {
		override val policyDtoClass: Class<NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto>
			get() = NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto::class.java
	}

	object NAV_ANSATT_TILGANG_TIL_MODIA_V1 :
		PolicyId<NavAnsattTilgangTilModiaPolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattTilgangTilModiaPolicyInputV1Dto>
			get() = NavAnsattTilgangTilModiaPolicyInputV1Dto::class.java
	}

	object EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1 :
		PolicyId<EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto>() {
		override val policyDtoClass: Class<EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto>
			get() = EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto::class.java
	}

	object NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1 :
		PolicyId<NavAnsattTilgangTilNavEnhetPolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattTilgangTilNavEnhetPolicyInputV1Dto>
			get() = NavAnsattTilgangTilNavEnhetPolicyInputV1Dto::class.java
	}

	object NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1 :
		PolicyId<NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto>
			get() = NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto::class.java
	}

	object NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1 :
		PolicyId<NavAnsattBehandleFortroligBrukerePolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattBehandleFortroligBrukerePolicyInputV1Dto>
			get() = NavAnsattBehandleFortroligBrukerePolicyInputV1Dto::class.java
	}

	object NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1 :
		PolicyId<NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto>
			get() = NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto::class.java
	}

	object NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1 :
		PolicyId<NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto>() {
		override val policyDtoClass: Class<NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto>
			get() = NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto::class.java
	}
}
