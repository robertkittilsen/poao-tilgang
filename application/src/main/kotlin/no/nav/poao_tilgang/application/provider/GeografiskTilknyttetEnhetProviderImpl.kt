package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.norg.NorgClient
import no.nav.poao_tilgang.application.client.pdl.GeografiskTilknytning
import no.nav.poao_tilgang.application.client.pdl.PdlClient
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import org.springframework.stereotype.Component

@Component
class GeografiskTilknyttetEnhetProviderImpl(
	private val pdlClient: PdlClient,
	private val norgClient: NorgClient
) : GeografiskTilknyttetEnhetProvider {

	override fun hentGeografiskTilknytetEnhet(norskIdent: NorskIdent): NavEnhetId? {
		val brukerInfo = pdlClient.hentBrukerInfo(norskIdent)

		return brukerInfo.geografiskTilknytning
			?.let { utledGeografiskTilknytningNr(it) }
			?.let { norgClient.hentTilhorendeEnhet(it) }
	}

	private fun utledGeografiskTilknytningNr(geografiskTilknytning: GeografiskTilknytning): String? {
		return when (geografiskTilknytning.gtType) {
			"BYDEL" -> geografiskTilknytning.gtBydel
			"KOMMUNE" -> geografiskTilknytning.gtKommune
			else -> null
		}
	}
}
