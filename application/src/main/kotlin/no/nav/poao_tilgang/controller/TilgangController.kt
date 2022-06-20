package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.api.dto.DecisionDto
import no.nav.poao_tilgang.api.dto.DecisionType
import no.nav.poao_tilgang.api.dto.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.api.dto.TilgangResponse
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.service.AuthService
import no.nav.poao_tilgang.service.TilgangService
import no.nav.poao_tilgang.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tilgang")
class TilgangController(
	private val authService: AuthService, private val tilgangService: TilgangService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/modia")
	fun harTilgangTilModia(@RequestBody request: HarTilgangTilModiaRequest): TilgangResponse {
		authService.verifyRequestIsMachineToMachine()

		val decision = tilgangService.harTilgangTilModia(request.navIdent)

		return mapTilResponse(decision)
	}

	private fun mapTilResponse(decision: Decision): TilgangResponse {
		val decisionDto = when (decision) {
			Decision.Permit -> DecisionDto(
				type = DecisionType.PERMIT, message = null, reason = null
			)
			is Decision.Deny -> DecisionDto(
				type = DecisionType.DENY, message = decision.message, reason = decision.reason.name
			)
		}
		return TilgangResponse(decisionDto)

	}
}
