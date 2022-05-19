package no.nav.poao_tilgang.service

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.client.microsoft_graph.MicrosoftGraphClient
import no.nav.poao_tilgang.domain.AdGruppe
import no.nav.poao_tilgang.domain.AzureObjectId
import no.nav.poao_tilgang.utils.CacheUtils.tryCacheFirstNotNull
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class AdGruppeService(
	private val microsoftGraphClient: MicrosoftGraphClient
) {

	private val navIdentToAzureIdCache = Caffeine.newBuilder()
		.maximumSize(10_000)
		.build<String, AzureObjectId>()

	private val azureIdToAdGroupsCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(15))
		.maximumSize(10_000)
		.build<AzureObjectId, List<AdGruppe>>()

	fun hentAdGrupper(navIdent: String): List<AdGruppe> {
		val azureId = hentAzureIdWithCache(navIdent)

		return hentAdGrupperWithCache(azureId)
	}

	private fun hentAdGrupperWithCache(azureId: AzureObjectId): List<AdGruppe> {
		return tryCacheFirstNotNull(azureIdToAdGroupsCache, azureId) {
			microsoftGraphClient.hentAdGrupper(azureId).map {
				AdGruppe(it.id, it.name)
			}
		}
	}

	private fun hentAzureIdWithCache(navIdent: String): AzureObjectId {
		return tryCacheFirstNotNull(navIdentToAzureIdCache, navIdent) { microsoftGraphClient.hentAzureId(navIdent) }
	}

}
