package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.axsys.AxsysClient
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.provider.NavEnhetTilgang
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import org.springframework.stereotype.Component

@Component
class NavEnhetTilgangProviderImpl(
	private val axsysClient: AxsysClient
) : NavEnhetTilgangProvider {

	override fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang> {
		return axsysClient.hentTilganger(navIdent)
			.map {
				NavEnhetTilgang(
					enhetId = it.enhetId,
					enhetNavn = it.enhetNavn,
					temaer = it.temaer
				)
			}
	}

}
