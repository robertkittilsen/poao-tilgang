package no.nav.poao_tilgang.poao_tilgang_test_core

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.*



data class Providers(
	val navContext: NavContext = NavContext(),
	val toggleProvider: ToggleProvider = ToggleProviderImpl(),
	val skjermetPersonProvider: SkjermetPersonProvider = SkjermetPersonProviderImpl(navContext),
	val oppfolgingsenhetProvider: OppfolgingsenhetProvider = OppfolgingsenhetProviderImpl(navContext),
	val navEnhetTilgangProvider: NavEnhetTilgangProvider = NavEnhetTilgangProviderImpl(navContext),
	val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider = GeografiskTilknyttetEnhetProviderImpl(navContext),
	val diskresjonskodeProvider: DiskresjonskodeProvider = DiskresjonskodeProviderImpl(navContext),
	val adGruppeProvider: AdGruppeProvider = AdGruppeProviderImpl(navContext),
	val abacProvider: AbacProvider = AbacProviderImpl(),
)
class ToggleProviderImpl : ToggleProvider {
	override fun brukAbacDecision(): Boolean {
		return false
	}
}

class SkjermetPersonProviderImpl(private  val navContext: NavContext) : SkjermetPersonProvider {
	override fun erSkjermetPerson(norskIdent: String): Boolean {
		return navContext.privatBrukere.get(norskIdent)?.erSkjermet ?: true
	}

	override fun erSkjermetPerson(norskeIdenter: List<String>): Map<String, Boolean> {
		return norskeIdenter.map { it to erSkjermetPerson(it) }.toMap()
	}
}

class OppfolgingsenhetProviderImpl(private val navContext: NavContext) : OppfolgingsenhetProvider {
	override fun hentOppfolgingsenhet(norskIdent: NorskIdent): NavEnhetId? {
		return navContext.privatBrukere.get(norskIdent)?.oppfolgingsenhet
	}
}

class NavEnhetTilgangProviderImpl(private val navContext: NavContext): NavEnhetTilgangProvider {
	override fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang> {
		return navContext.navAnsatt.get(navIdent)?.enheter?.toList() ?: emptyList()
	}
}

class GeografiskTilknyttetEnhetProviderImpl(private val navContext: NavContext): GeografiskTilknyttetEnhetProvider {
	override fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent): NavEnhetId? {
		return navContext.privatBrukere.get(norskIdent)?.oppfolgingsenhet //for enklere oppset
	}
}

class DiskresjonskodeProviderImpl(private val  navContext: NavContext): DiskresjonskodeProvider {
	override fun hentDiskresjonskode(norskIdent: String): Diskresjonskode? {
		return navContext.privatBrukere.get(norskIdent)?.diskresjonskode
	}
}

class AdGruppeProviderImpl(private val navContext: NavContext): AdGruppeProvider {
	override fun hentAdGrupper(navAnsattAzureId: AzureObjectId): List<AdGruppe> {
		return navContext.navAnsatt.get(navAnsattAzureId)?.adGrupper?.toList() ?: emptyList()
	}

	override fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent {
		return navContext.navAnsatt.get(navAnsattAzureId)!!.navIdent
	}

	override fun hentAzureIdMedNavIdent(navIdent: NavIdent): AzureObjectId {
		return navContext.navAnsatt.get(navIdent)!!.azureObjectId
	}

	override fun hentTilgjengeligeAdGrupper(): AdGrupper {
		return tilgjengligeAdGrupper
	}
}
class AbacProviderImpl(): AbacProvider {
	override fun harVeilederTilgangTilPerson(
		veilederIdent: String,
		tilgangType: TilgangType,
		eksternBrukerId: String
	): Boolean {
		return true
	}

	override fun harVeilederTilgangTilNavEnhet(veilederIdent: String, navEnhetId: String): Boolean {
		return true
	}

	override fun harVeilederTilgangTilNavEnhetMedSperre(veilederIdent: String, navEnhetId: String): Boolean {
		return true
	}

}
