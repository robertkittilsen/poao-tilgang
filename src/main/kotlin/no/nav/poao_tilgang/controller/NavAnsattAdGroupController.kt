package no.nav.poao_tilgang.controller

import no.nav.security.token.support.core.api.Protected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/ad-group")
class NavAnsattAdGroupController {

	@Protected
	@GetMapping(params = ["navIdent"])
	fun hentAdGrupperForNavAnsatt(@RequestParam("navIdent") navIdent: String): List<AdGroupDto> {
		return emptyList()
	}

	@Protected
	@GetMapping
	fun hentInnloggetNavAnsattAdGrupper(): List<AdGroupDto> {
		return emptyList()
	}

	data class AdGroupDto(
		val id: UUID,
		val name: String
	)

}
