package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import org.springframework.stereotype.Service

@Service
class DiskresjonskodeProviderImpl : DiskresjonskodeProvider {

	override fun hentDiskresjonskode(norskIdent: String): Diskresjonskode? {
		TODO("Not yet implemented")
	}

}
