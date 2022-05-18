package no.nav.poao_tilgang.service

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.client.microsoft_graph.MicrosoftGraphClient
import no.nav.poao_tilgang.domain.AdGroup
import no.nav.poao_tilgang.domain.AzureObjectId
import no.nav.poao_tilgang.utils.CacheUtils.tryCacheFirstNotNull
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class MicrosoftGraphService(
	private val microsoftGraphClient: MicrosoftGraphClient
) {

	private val navIdentToAzureIdCache = Caffeine.newBuilder()
		.maximumSize(10_000)
		.build<String, AzureObjectId>()

	private val azureIdToAdGroupsCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(15))
		.maximumSize(10_000)
		.build<AzureObjectId, List<AdGroup>>()

	fun hentAdRoller(navIdent: String): List<AdGroup> {
		val azureId = hentAzureIdWithCache(navIdent)

		return hentAdRollerWithCache(azureId)
	}

	private fun hentAdRollerWithCache(azureId: AzureObjectId): List<AdGroup> {
		return tryCacheFirstNotNull(azureIdToAdGroupsCache, azureId) {
			microsoftGraphClient.hentAdGrupper(azureId).map {
				AdGroup(it.id, it.navn)
			}
		}
	}

	private fun hentAzureIdWithCache(navIdent: String): AzureObjectId {
		return tryCacheFirstNotNull(navIdentToAzureIdCache, navIdent) { microsoftGraphClient.hentAzureId(navIdent) }
	}

}
