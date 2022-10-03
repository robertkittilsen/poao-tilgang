package no.nav.poao_tilgang.application.provider

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.client.veilarbarena.VeilarbarenaClient
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNullable
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OppfolgingsenhetProviderImpl(
	private val veilarbarenaClient: VeilarbarenaClient
) : OppfolgingsenhetProvider {

	private val norskIdentToOppfolgingsenhetCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
		.build<NorskIdent, NavEnhetId>()

	override fun hentOppfolgingsenhet(norskIdent: NorskIdent): NavEnhetId? {
		return tryCacheFirstNullable(norskIdentToOppfolgingsenhetCache, norskIdent) {
			return@tryCacheFirstNullable veilarbarenaClient.hentBrukerOppfolgingsenhetId(norskIdent)
		}
	}

}
