package no.nav.poao_tilgang.application.provider

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.poao_tilgang.core.provider.AbacProvider
import org.springframework.stereotype.Component

@Component
class AbacProviderImpl(private val pep: Pep) : AbacProvider {

	override fun harVeilederTilgangTilPerson(veilederIdent: String, eksternBrukerId: String): Boolean {
		return pep.harVeilederTilgangTilPerson(NavIdent.of(veilederIdent), ActionId.WRITE, Fnr.of(eksternBrukerId))
	}

}
