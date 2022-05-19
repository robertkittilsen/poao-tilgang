package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.service.AdGruppeService
import no.nav.security.token.support.core.api.Protected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/ad-gruppe")
class AdGruppeController(
	private val adGruppeService: AdGruppeService
) {

	@Protected
	@GetMapping(params = ["navIdent"])
	fun hentAdGrupperForNavAnsatt(@RequestParam("navIdent") navIdent: String): List<AdGruppeDto> {
		return adGruppeService.hentAdGrupper(navIdent).map {
			AdGruppeDto(
				id = it.id,
				name = it.name
			)
		}
	}

	data class AdGruppeDto(
		val id: UUID,
		val name: String
	)

}
