package no.nav.poao_tilgang.application.provider

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClient
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNotNull
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.FORTROLIG_ADRESSE
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.GOSYS_NASJONAL
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.GOSYS_UTVIDBAR_TIL_NASJONAL
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.GOSYS_UTVIDET
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.MODIA_ADMIN
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.MODIA_GENERELL
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.MODIA_OPPFOLGING
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.PENSJON_UTVIDET
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.STRENGT_FORTROLIG_ADRESSE
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.SYFO_SENSITIV
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class AdGruppeProviderImpl(
	private val microsoftGraphClient: MicrosoftGraphClient,
	@Value("\${ad-gruppe-id.fortrolig-adresse}") private val adGruppeIdFortroligAdresse: UUID,
	@Value("\${ad-gruppe-id.strengt-fortrolig-adresse}") private val adGruppeIdStrengtFortroligAdresse: UUID,
	@Value("\${ad-gruppe-id.modia-admin}") private val adGruppeIdModiaAdmin: UUID,
	@Value("\${ad-gruppe-id.modia-oppfolging}") private val adGruppeIdModiaOppfolging: UUID,
	@Value("\${ad-gruppe-id.modia-generell}") private val adGruppeIdModiaGenerell: UUID,
	@Value("\${ad-gruppe-id.gosys-nasjonal}") private val adGruppeIdGosysNasjonal: UUID,
	@Value("\${ad-gruppe-id.gosys-utvidbar-til-nasjonal}") private val adGruppeIdGosysUtvidbarTilNasjonal: UUID,
	@Value("\${ad-gruppe-id.gosys-utvidet}") private val adGruppeIdGosysUtvidet: UUID,
	@Value("\${ad-gruppe-id.syfo-sensitiv}") private val adGruppeIdSyfoSensitiv: UUID,
	@Value("\${ad-gruppe-id.pensjon-utvidet}") private val adGruppeIdPensjonUtvidet: UUID
) : AdGruppeProvider {

	private val tilgjengligeAdGrupper = AdGrupper(
		fortroligAdresse = AdGruppe(adGruppeIdFortroligAdresse, FORTROLIG_ADRESSE),
		strengtFortroligAdresse = AdGruppe(adGruppeIdStrengtFortroligAdresse, STRENGT_FORTROLIG_ADRESSE),
		modiaAdmin = AdGruppe(adGruppeIdModiaAdmin, MODIA_ADMIN),
		modiaOppfolging = AdGruppe(adGruppeIdModiaOppfolging, MODIA_OPPFOLGING),
		modiaGenerell = AdGruppe(adGruppeIdModiaGenerell, MODIA_GENERELL),
		gosysNasjonal = AdGruppe(adGruppeIdGosysNasjonal, GOSYS_NASJONAL),
		gosysUtvidbarTilNasjonal = AdGruppe(adGruppeIdGosysUtvidbarTilNasjonal, GOSYS_UTVIDBAR_TIL_NASJONAL),
		gosysUtvidet = AdGruppe(adGruppeIdGosysUtvidet, GOSYS_UTVIDET),
		syfoSensitiv = AdGruppe(adGruppeIdSyfoSensitiv, SYFO_SENSITIV),
		pensjonUtvidet = AdGruppe(adGruppeIdPensjonUtvidet, PENSJON_UTVIDET)
	)


	private val navIdentToAzureIdCache = Caffeine.newBuilder()
		.maximumSize(10_000)
		.build<String, AzureObjectId>()

	// TODO: Bruk heller List<UUID> for å redusere minnebruk
	private val navAnsattAzureIdToAdGroupsCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(15))
		.maximumSize(10_000)
		.build<AzureObjectId, List<AdGruppe>>()

	private val adGruppeIdToAdGruppeCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.build<AzureObjectId, AdGruppe>()

	override fun hentAdGrupper(navIdent: String): List<AdGruppe> {
		val azureId = hentAzureIdWithCache(navIdent)

		return hentAdGrupperForNavAnsattWithCache(azureId)
	}

	override fun hentAdGrupper(azureId: AzureObjectId): List<AdGruppe> {
		return hentAdGrupperForNavAnsattWithCache(azureId)
	}

	override fun hentTilgjengeligeAdGrupper(): AdGrupper {
		return tilgjengligeAdGrupper
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
		return tryCacheFirstNotNull(navIdentToAzureIdCache, navIdent) {
			microsoftGraphClient.hentAzureIdForNavAnsatt(
				navIdent
			)
		}
	}

}
