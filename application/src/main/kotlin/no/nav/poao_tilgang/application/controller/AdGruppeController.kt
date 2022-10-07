package no.nav.poao_tilgang.application.controller

import no.nav.poao_tilgang.api.dto.request.HentAdGrupperForBrukerRequest
import no.nav.poao_tilgang.api.dto.response.AdGruppeDto
import no.nav.poao_tilgang.api.dto.response.HentAdGrupperForBrukerResponse
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ad-gruppe")
class AdGruppeController(
	private val adGruppeProvider: AdGruppeProvider,
	private val authService: AuthService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun hentAlleAdGrupperForBruker(@RequestBody request: HentAdGrupperForBrukerRequest): HentAdGrupperForBrukerResponse {
		authService.verifyRequestIsMachineToMachine()

		return adGruppeProvider.hentAdGrupper(request.navAnsattAzureId)
			.map { mapTilDto(it) }
	}

	private fun mapTilDto(adGruppe: AdGruppe): AdGruppeDto {
		return AdGruppeDto(
			adGruppe.id,
			adGruppe.navn
		)
	}

}
