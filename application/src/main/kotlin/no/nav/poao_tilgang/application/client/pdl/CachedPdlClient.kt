package no.nav.poao_tilgang.application.client.pdl

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNotNull
import no.nav.poao_tilgang.core.domain.NorskIdent
import java.time.Duration

class CachedPdlClient(
	private val pdlClient: PdlClient
) : PdlClient {

	private val norskIdentToBrukerInfoCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
		.build<NorskIdent, BrukerInfo>()

	override fun hentBrukerInfo(brukerIdent: String): BrukerInfo {
		return tryCacheFirstNotNull(norskIdentToBrukerInfoCache, brukerIdent) {
			pdlClient.hentBrukerInfo(brukerIdent)
		}
	}

}
