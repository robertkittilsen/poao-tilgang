package no.nav.poao_tilgang.service

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.policy.ModiaPolicy
import org.springframework.stereotype.Service

@Service
class TilgangService(
	private val modiaPolicy: ModiaPolicy
) {

	fun harTilgangTilModia(navIdent: NavIdent): Decision {
		return modiaPolicy.harTilgang(navIdent)
	}

}
