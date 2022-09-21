package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.pdl.Adressebeskyttelse
import no.nav.poao_tilgang.application.client.pdl.PdlClient
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import org.springframework.stereotype.Service

@Service
class DiskresjonskodeProviderImpl(
	private val pdlClient: PdlClient
) : DiskresjonskodeProvider {

	override fun hentDiskresjonskode(norskIdent: String): Diskresjonskode? {
		return pdlClient.hentBrukerInfo(norskIdent)
			.adressebeskyttelse?.let { tilDiskresjonskode(it) }
	}

	private fun tilDiskresjonskode(adressebeskyttelse: Adressebeskyttelse): Diskresjonskode {
		return when(adressebeskyttelse) {
			Adressebeskyttelse.FORTROLIG -> Diskresjonskode.FORTROLIG
			Adressebeskyttelse.STRENGT_FORTROLIG -> Diskresjonskode.STRENGT_FORTROLIG
			Adressebeskyttelse.STRENGT_FORTROLIG_UTLAND -> Diskresjonskode.STRENGT_FORTROLIG_UTLAND
		}
	}

}
