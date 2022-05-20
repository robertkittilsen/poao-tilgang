package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.service.AuthService
import no.nav.poao_tilgang.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/skjermet-person")
class SkjermetPersonController(
	private val authService: AuthService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun erSkjermet(): List<String> {
		authService.verifyRequestIsMachineToMachine()

		return emptyList()
	}

}
