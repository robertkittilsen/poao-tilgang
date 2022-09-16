package no.nav.poao_tilgang.application.controller

import no.nav.poao_tilgang.api.dto.request.ErSkjermetBulkRequest
import no.nav.poao_tilgang.api.dto.response.HentErSkjermetPersonBulkResponse
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.core.provider.SkjermetPersonProvider
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/skjermet-person")
class SkjermetPersonController(
	private val authService: AuthService,
	private val skjermetPersonProvider: SkjermetPersonProvider
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun erSkjermet(@RequestBody request: ErSkjermetBulkRequest): HentErSkjermetPersonBulkResponse {
		authService.verifyRequestIsMachineToMachine()

		return skjermetPersonProvider.erSkjermetPerson(request.norskeIdenter)
	}

	@Deprecated("Bruk heller endepunktet ovenfor")
	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/bulk")
	fun erSkjermetBulk(@RequestBody request: ErSkjermetBulkRequest): HentErSkjermetPersonBulkResponse {
		authService.verifyRequestIsMachineToMachine()

		return skjermetPersonProvider.erSkjermetPerson(request.norskeIdenter)
	}

}
