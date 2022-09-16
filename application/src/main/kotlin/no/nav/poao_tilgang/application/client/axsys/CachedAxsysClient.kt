package no.nav.poao_tilgang.application.client.axsys

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration

private typealias NavIdent = String

class CachedAxsysClient(
	private val axsysClient: AxsysClient
) : AxsysClient {

	private val cache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
		.build<NavIdent, List<EnhetTilgang>> { navIdent ->
			axsysClient.hentTilganger(navIdent)
		}

	override fun hentTilganger(navIdent: String) =
		cache.get(navIdent) ?: throw IllegalStateException("Fant ikke brukerident")

}
