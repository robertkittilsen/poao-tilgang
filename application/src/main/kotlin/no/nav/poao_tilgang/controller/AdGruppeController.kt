package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.provider_impl.AdGruppeService
import no.nav.poao_tilgang.service.AuthService
import no.nav.poao_tilgang.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/ad-gruppe")
class AdGruppeController(
	private val adGruppeService: AdGruppeService,
	private val authService: AuthService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun hentAdGrupperForNavAnsatt(@RequestBody request: HentAdGrupperForNavAnsattRequest): List<AdGruppeDto> {
		authService.verifyRequestIsMachineToMachine()

		return adGruppeService.hentAdGrupper(request.navIdent).map {
			AdGruppeDto(
				id = it.id,
				name = it.name
			)
		}
	}

	data class HentAdGrupperForNavAnsattRequest(
		val navIdent: String
	)

	data class AdGruppeDto(
		val id: UUID,
		val name: String
	)

}
