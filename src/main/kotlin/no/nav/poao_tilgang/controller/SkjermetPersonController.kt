package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.service.AuthService
import no.nav.poao_tilgang.service.SkjermetPersonService
import no.nav.poao_tilgang.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/skjermet-person")
class SkjermetPersonController(
	private val authService: AuthService,
	private val skjermetPersonService: SkjermetPersonService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun erSkjermet(@RequestBody request: ErSkjermetRequest): ErSkjermetResponse {
		authService.verifyRequestIsMachineToMachine()

		val erSkjermet = skjermetPersonService.erSkjermetPerson(request.norskIdent)

		return ErSkjermetResponse(erSkjermet)
	}

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/bulk")
	fun erSkjermetBulk(@RequestBody request: ErSkjermetBulkRequest): Map<String, Boolean> {
		authService.verifyRequestIsMachineToMachine()

		return skjermetPersonService.erSkjermetPerson(request.norskeIdenter)
	}

	data class ErSkjermetRequest(
		val norskIdent: String
	)

	data class ErSkjermetResponse(
		val erSkjermet: Boolean
	)

	data class ErSkjermetBulkRequest(
		val norskeIdenter: List<String>
	)

}
