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

	private val navAnsattAzureIdToAdGroupsCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(15))
		.maximumSize(10_000)
		.build<AzureObjectId, List<AdGruppe>>()

	private val adGruppeIdToAdGruppeCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.build<AzureObjectId, AdGruppe>()

	fun hentAdGrupper(navIdent: String): List<AdGruppe> {
		val azureId = hentAzureIdWithCache(navIdent)

		return hentAdGrupperForNavAnsattWithCache(azureId)
	}

	private fun hentAdGrupperForNavAnsattWithCache(azureId: AzureObjectId): List<AdGruppe> {
		return tryCacheFirstNotNull(navAnsattAzureIdToAdGroupsCache, azureId) {
			val gruppeIder = microsoftGraphClient.hentAdGrupperForNavAnsatt(azureId)

			hentAdGrupperWithCache(gruppeIder)
		}
	}

	private fun hentAdGrupperWithCache(adGruppeIder: List<AzureObjectId>): List<AdGruppe> {
		val cachedGroups = mutableListOf<AdGruppe>()
		val missingGroups = mutableListOf<AzureObjectId>()

		adGruppeIder.forEach {
			val gruppe = adGruppeIdToAdGruppeCache.getIfPresent(it)

			if (gruppe != null) {
				cachedGroups.add(gruppe)
			} else {
				missingGroups.add(it)
			}
		}

		if (missingGroups.isEmpty()) {
			return cachedGroups
		}

		val adGrupper = microsoftGraphClient.hentAdGrupper(missingGroups)

		adGrupper.forEach {
			val gruppe = AdGruppe(it.id, it.name)

			adGruppeIdToAdGruppeCache.put(it.id, gruppe)
			cachedGroups.add(gruppe)
		}

		return cachedGroups
	}

	private fun hentAzureIdWithCache(navIdent: String): AzureObjectId {
		return tryCacheFirstNotNull(navIdentToAzureIdCache, navIdent) { microsoftGraphClient.hentAzureIdForNavAnsatt(navIdent) }
	}

}
