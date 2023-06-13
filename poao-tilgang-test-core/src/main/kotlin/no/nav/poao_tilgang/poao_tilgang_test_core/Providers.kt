package no.nav.poao_tilgang.poao_tilgang_test_core

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.*



data class Providers(
	val navModell: NavModell = NavModell(),
	val toggleProvider: ToggleProvider = ToggleProviderImpl(),
	val skjermetPersonProvider: SkjermetPersonProvider = SkjermetPersonProviderImpl(navModell),
	val oppfolgingsenhetProvider: OppfolgingsenhetProvider = OppfolgingsenhetProviderImpl(navModell),
	val navEnhetTilgangProvider: NavEnhetTilgangProvider = NavEnhetTilgangProviderImpl(navModell),
	val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider = GeografiskTilknyttetEnhetProviderImpl(navModell),
	val diskresjonskodeProvider: DiskresjonskodeProvider = DiskresjonskodeProviderImpl(navModell),
	val adGruppeProvider: AdGruppeProvider = AdGruppeProviderImpl(navModell),
	val abacProvider: AbacProvider = AbacProviderImpl(),
)
{}
class ToggleProviderImpl : ToggleProvider {
	override fun brukAbacDesision(): Boolean {
		return false
	}
}

class SkjermetPersonProviderImpl(private  val navModell: NavModell) : SkjermetPersonProvider {
	override fun erSkjermetPerson(norskIdent: String): Boolean {
		return navModell.hentEksternBruker(norskIdent)?.erSkjermet ?: true
	}

	override fun erSkjermetPerson(norskeIdenter: List<String>): Map<String, Boolean> {
		return norskeIdenter.map { it to erSkjermetPerson(it) }.toMap()
	}
}

class OppfolgingsenhetProviderImpl(private val navModell: NavModell) : OppfolgingsenhetProvider {
	override fun hentOppfolgingsenhet(norskIdent: NorskIdent): NavEnhetId? {
		return navModell.hentEksternBruker(norskIdent)?.oppfolgingsenhet
	}
}

class NavEnhetTilgangProviderImpl(private val navModell: NavModell): NavEnhetTilgangProvider {
	override fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang> {
		return navModell.henNavAnsatt(navIdent)?.enheter?.toList() ?: emptyList()
	}
}

class GeografiskTilknyttetEnhetProviderImpl(private val navModell: NavModell): GeografiskTilknyttetEnhetProvider {
	override fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent): NavEnhetId? {
		return navModell.hentEksternBruker(norskIdent)?.oppfolgingsenhet //for enklere oppset
	}
}

class DiskresjonskodeProviderImpl(private val  navModell: NavModell): DiskresjonskodeProvider {
	override fun hentDiskresjonskode(norskIdent: String): Diskresjonskode? {
		return navModell.hentEksternBruker(norskIdent)?.diskresjonskode
	}
}

class AdGruppeProviderImpl(private val navModell: NavModell): AdGruppeProvider {
	override fun hentAdGrupper(navAnsattAzureId: AzureObjectId): List<AdGruppe> {
		return navModell.henNavAnsatt(navAnsattAzureId)?.adGrupper?.toList() ?: emptyList()
	}

	override fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent {
		return navModell.henNavAnsatt(navAnsattAzureId)!!.navIdent
	}

	override fun hentAzureIdMedNavIdent(navIdent: NavIdent): AzureObjectId {
		return navModell.henNavAnsatt(navIdent)!!.azureObjectId
	}

	override fun hentTilgjengeligeAdGrupper(): AdGrupper {
		return tilgjengligeAdGrupper
	}
}
class AbacProviderImpl(): AbacProvider {
	//TODO: mocke denne eller implemeter togle/config for å ikke logge diff?
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
