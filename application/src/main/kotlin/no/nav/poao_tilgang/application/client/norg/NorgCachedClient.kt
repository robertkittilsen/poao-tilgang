package no.nav.poao_tilgang.application.client.norg

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNullable
import no.nav.poao_tilgang.core.domain.NavEnhetId
import java.util.concurrent.TimeUnit

class NorgCachedClient(private val norgClient: NorgClient) : NorgClient {

    private val hentTilhorendeNavEnhetIdCache: Cache<String, NavEnhetId> = Caffeine.newBuilder()
        .expireAfterWrite(12, TimeUnit.HOURS)
        .build()

	override fun hentTilhorendeEnhet(geografiskTilknytning: String): NavEnhetId? {
        return tryCacheFirstNullable(hentTilhorendeNavEnhetIdCache, geografiskTilknytning) {
			return@tryCacheFirstNullable norgClient.hentTilhorendeEnhet(geografiskTilknytning)
		}
	}
}
