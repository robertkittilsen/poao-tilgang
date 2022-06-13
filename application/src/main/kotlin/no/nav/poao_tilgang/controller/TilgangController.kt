package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.service.AuthService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tilgang")
class TilgangController(
	private val authService: AuthService
) {

}
