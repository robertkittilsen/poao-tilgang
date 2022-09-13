package no.nav.poao_tilgang.application.controller

import no.nav.poao_tilgang.application.provider.AdGruppeProvider
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/ad-gruppe")
class AdGruppeController(
	private val adGruppeProvider: AdGruppeProvider,
	private val authService: AuthService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/gammel")
	fun hentAdGrupperForNavAnsatt(@RequestBody request: HentAdGrupperForNavAnsattRequest): List<AdGruppeDto> {
		authService.verifyRequestIsMachineToMachine()

		return adGruppeProvider.hentAdGrupper(request.navIdent).map {
			AdGruppeDto(
				id = it.id,
				name = it.name
			)
		}
	}

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun hentAdGrupperForBruker(@RequestBody request: HentAdGrupperForBrukerRequest): List<AdGruppeDto> {
		authService.verifyRequestIsMachineToMachine()

		return adGruppeProvider.hentAdGrupper(request.navAnsattAzureId).map {
			AdGruppeDto(
				id = it.id,
				name = it.name
			)
		}
	}

	data class HentAdGrupperForBrukerRequest(
		val navAnsattAzureId: AzureObjectId
	)

	data class HentAdGrupperForNavAnsattRequest(
		val navIdent: String
	)

	data class AdGruppeDto(
		val id: UUID,
		val name: String
	)

}
