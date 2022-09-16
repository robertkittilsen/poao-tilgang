package no.nav.poao_tilgang.api.dto.request

data class EvaluatePoliciesRequest<I> (
	val requests: List<PolicyEvaluationRequestDto<I>>
)

