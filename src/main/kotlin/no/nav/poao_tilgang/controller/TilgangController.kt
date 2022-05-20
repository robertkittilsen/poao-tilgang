package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.domain.Decision
import no.nav.poao_tilgang.domain.DecisionReasonType
import no.nav.poao_tilgang.domain.Policy
import no.nav.poao_tilgang.service.AuthService
import no.nav.poao_tilgang.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/tilgang")
class TilgangController(
	private val authService: AuthService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun harTilgang(@RequestBody tilgangRequest: TilgangRequest): TilgangResponse {
		authService.verifyRequestIsMachineToMachine()

		TODO()
	}

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/bulk")
	fun harTilgangBulk(@RequestBody tilgangRequest: TilgangBulkRequest): TilgangResponse {
		authService.verifyRequestIsMachineToMachine()

		TODO()
	}

}

data class TilgangRequest(
	val requestId: UUID, // Unik id pr request, alternativ til Nav-Call-Id
	val requester: Requester,
	val resource: Resource,
	val policy: Policy,
	val auditLogMetadata: AuditLogMetadata?
)

data class TilgangBulkRequest(
	val requestId: UUID, // Unik id pr request, alternativ til Nav-Call-Id
	val requester: Requester,
	val resources: List<Resource>,
	val policy: Policy,
	val auditLogMetadata: AuditLogMetadata?
)

data class TilgangResponse(
	val decision: PolicyDecision,
)

data class TilgangBulkResponse(
	// Mangler måte å identifisere hvilke decision som hører til hvilken resource
	val decisions: List<PolicyDecision>,
)

data class PolicyDecision(
	val policy: Policy,
	val decision: Decision,
	val reason: Reason,
)

data class Reason(
	val type: DecisionReasonType,
	val description: String
)

data class AuditLogMetadata(
	val todo: String
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
	AD_GROUP_OBJECT_ID("AD_GROUP_OBJECT_ID"),
	AD_GROUP_NAME("AD_GROUP_NAME"),
}

data class Requester(
	val identifier: String,
	val identifierType: RequesterIdentifierType
)

data class Resource(
	val identifier: String,
	val identifierType: ResourceIdentifierType
)
