package no.nav.poao_tilgang.application.provider

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.provider.AbacProvider
import org.springframework.stereotype.Component

@Component
class AbacProviderImpl(private val pep: Pep) : AbacProvider {

	override fun harVeilederTilgangTilPerson(
		veilederIdent: String,
		tilgangType: TilgangType,
		eksternBrukerId: String
	): Boolean {

		val action = when (tilgangType) {
			TilgangType.LESE -> ActionId.READ
			TilgangType.SKRIVE -> ActionId.WRITE
		}

		return pep.harVeilederTilgangTilPerson(NavIdent.of(veilederIdent), action, Fnr.of(eksternBrukerId))
	}

	override fun harVeilederTilgangTilNavEnhet(veilederIdent: String, navEnhetId: String): Boolean {
		return pep.harVeilederTilgangTilEnhet(NavIdent.of(veilederIdent), EnhetId.of(navEnhetId))
	}

}
