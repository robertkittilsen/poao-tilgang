package no.nav.poao_tilgang.application.controller

import no.nav.poao_tilgang.api.dto.request.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.TilgangResponse
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.service.TilgangService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tilgang")
class TilgangController(
	private val authService: AuthService,
	private val tilgangService: TilgangService
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

		return TilgangResponse(decisionDto)

	}
}
