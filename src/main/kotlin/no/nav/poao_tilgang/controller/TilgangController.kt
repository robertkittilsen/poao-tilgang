package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.domain.DecisionReasonType
import no.nav.poao_tilgang.domain.Policy
import no.nav.poao_tilgang.domain.Decision
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/tilgang")
class TilgangController {

	@PostMapping
	fun harTilgang(@RequestBody tilgangRequest: TilgangRequest): TilgangResponse {
		TODO()
	}

}

data class TilgangRequest(
	val id: UUID, // Unik id pr request, alternativ til Nav-Call-Id
	val requester: Requester,
	val resource: Resource,
	val policy: Policy
)

data class TilgangResponse(
	val id: UUID, // Samme id som sendt i request
	val decision: PolicyDecision,
)

data class PolicyDecision(
	val policy: Policy,
	val decision: Decision,
	val reason: Reason
)

data class Reason(
	val type: DecisionReasonType,
	val description: String
)

enum class RequesterIdentifierType(val type: String) {
	NORSK_IDENT("NORSK_IDENT"),
	AKTOR_ID("AKTOR_ID"),
	NAV_IDENT("NAV_IDENT"),
}

enum class ResourceIdentifierType(val type: String) {
	NORSK_IDENT("NORSK_IDENT"),
	AKTOR_ID("AKTOR_ID"),
	NAV_ENHET_ID("NAV_IDENT"),
	AD_GROUP("AD_GROUP")
}

data class Requester(
	val identifier: String,
	val identifierType: RequesterIdentifierType
)

data class Resource(
	val identifier: String,
	val identifierType: ResourceIdentifierType
)
