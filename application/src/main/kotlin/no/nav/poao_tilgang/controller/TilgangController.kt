package no.nav.poao_tilgang.controller

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
	private val authService: AuthService,
	private val tilgangService: TilgangService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/modia")
	fun harTilgangTilModia(@RequestBody request: HarTilgangTilModiaRequest): TilgangResponse  {
		authService.verifyRequestIsMachineToMachine()

		val decision = tilgangService.harTilgangTilModia(request.navIdent)

		return TilgangResponse(decision)
	}

}

data class HarTilgangTilModiaRequest(
	val navIdent: String
)

data class TilgangResponse(
	val decision: Decision
)
