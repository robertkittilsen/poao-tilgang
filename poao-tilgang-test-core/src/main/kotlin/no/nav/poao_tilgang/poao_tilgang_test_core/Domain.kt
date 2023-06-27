package no.nav.poao_tilgang.poao_tilgang_test_core

import net.datafaker.Faker
import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.NavEnhetTilgang
import no.nav.poao_tilgang.poao_tilgang_test_core.fnr_generator.FoedselsnummerGenerator
import java.util.*

internal val faker = Faker()
internal val fnrGenerator = FoedselsnummerGenerator()
class PrivatBruker(
	val norskIdent: NorskIdent = fnrGenerator.foedselsnummer().asString,
	val name: String = faker.name().fullName(), //for å gjøre det enklere å se hva som fieler. letter å se på en en lang rekke nummer.
	var erSkjermet: Boolean = false,
	var oppfolgingsenhet: NavEnhetId? = nyNavEnhet(),
	var diskresjonskode: Diskresjonskode? = null
)

class NavAnsatt(
	val navIdent: NavIdent = nyNavIdent(),
	val azureObjectId: AzureObjectId = UUID.randomUUID(),
	val name: String = faker.name().fullName(),  //for å gjøre det enklere å se hva som fieler. letter å se på en en lang rekke nummer.
) {
	val enheter: MutableSet<NavEnhetTilgang> = mutableSetOf()
	val adGrupper: MutableSet<AdGruppe> = mutableSetOf()
}


val tilgjengligeAdGrupper = AdGrupper(
	fortroligAdresse = AdGruppe(UUID.fromString("354ad832-93cd-47ca-ba60-cfd6288dfc55"), AdGruppeNavn.FORTROLIG_ADRESSE),
	strengtFortroligAdresse = AdGruppe(UUID.fromString("660c3c11-b1f7-4e54-93f6-cd4492206fe7"), AdGruppeNavn.STRENGT_FORTROLIG_ADRESSE),
	modiaAdmin = AdGruppe(UUID.fromString("d0cfadfa-8366-4639-b756-a42005bb380f"), AdGruppeNavn.MODIA_ADMIN),
	modiaOppfolging = AdGruppe(UUID.fromString("ebe5066a-a051-447a-8691-7b4a8b3ac0ae"), AdGruppeNavn.MODIA_OPPFOLGING),
	modiaGenerell = AdGruppe(UUID.fromString("9f95a39e-6b88-4454-9e8b-a8d2a4f950f0"), AdGruppeNavn.MODIA_GENERELL),
	gosysNasjonal = AdGruppe(UUID.fromString("017a0c2e-f953-464e-9306-6c7a6d92c82d"), AdGruppeNavn.GOSYS_NASJONAL),
	gosysUtvidbarTilNasjonal = AdGruppe(UUID.fromString("a837a9b4-4f05-421b-8fb7-1ca35e0302e0"), AdGruppeNavn.GOSYS_UTVIDBAR_TIL_NASJONAL),
	syfoSensitiv = AdGruppe(UUID.fromString("b6318312-a5e3-4e26-ac4c-08f8b86660e8"), AdGruppeNavn.SYFO_SENSITIV),
	egneAnsatte = AdGruppe(UUID.fromString("e44768ac-e68a-4dd1-b6ad-eebf5eb29924"), AdGruppeNavn.EGNE_ANSATTE),
	aktivitetsplanKvp = AdGruppe(UUID.fromString("259362c2-f7cd-4de7-b2dd-5a848bfdf61b"), AdGruppeNavn.AKTIVITETSPLAN_KVP)
)

class PrivatBrukere {
	private val eksterneBrukere = mutableMapOf<NorskIdent, PrivatBruker>()

	fun add(privatBruker: PrivatBruker) {
		if(eksterneBrukere.containsKey(privatBruker.norskIdent)){
			throw IllegalArgumentException("Bruker med norskIdent ${privatBruker.norskIdent} finnes allerede")
		}
		eksterneBrukere[privatBruker.norskIdent] = privatBruker
	}

	fun get(norskIdent: NorskIdent): PrivatBruker? {
		return eksterneBrukere[norskIdent]
	}

	fun ny(): PrivatBruker {
		val privatBruker = PrivatBruker()
		add(privatBruker)
		return privatBruker
	}
}

class NavAnsatte {
	private val navAnsatte = mutableMapOf<AzureObjectId, NavAnsatt>()
	private val navAnsatteMedNavIdent = mutableMapOf<NavIdent, NavAnsatt>()

	fun add(navAnsatt: NavAnsatt) {
		if (navAnsatte.containsKey(navAnsatt.azureObjectId)) {
			throw IllegalArgumentException("NavAnsatt med azureObjectId ${navAnsatt.azureObjectId} finnes allerede")
		}
		if (navAnsatteMedNavIdent.containsKey(navAnsatt.navIdent)) {
			throw IllegalArgumentException("NavAnsatt med navIdent ${navAnsatt.navIdent} finnes allerede")
		}
		navAnsatte[navAnsatt.azureObjectId] = navAnsatt
		navAnsatteMedNavIdent[navAnsatt.navIdent] = navAnsatt
	}

	fun get(azureObjectId: AzureObjectId): NavAnsatt? {
		return navAnsatte[azureObjectId]
	}
	fun get(navIdent: NavIdent): NavAnsatt? {
		return navAnsatteMedNavIdent[navIdent]
	}

	fun nyFor(privatBruker: PrivatBruker): NavAnsatt {
		val enhet = privatBruker.oppfolgingsenhet ?: throw IllegalArgumentException("EksternBruker må høre til en enhet")

		val navAnsatt = NavAnsatt()
		add(navAnsatt)
		navAnsatt.adGrupper.add(tilgjengligeAdGrupper.modiaOppfolging)
		navAnsatt.enheter.add(NavEnhetTilgang(enhet, "enhetNavn $enhet", emptyList()))
		return navAnsatt
	}

	fun nyNksAnsatt(): NavAnsatt {
		val navAnsatt = NavAnsatt()
		add(navAnsatt)
		navAnsatt.adGrupper.add(tilgjengligeAdGrupper.modiaOppfolging)
		navAnsatt.adGrupper.add(tilgjengligeAdGrupper.gosysNasjonal)
		navAnsatt.enheter.add(NavEnhetTilgang("0000", "NAV Viken", emptyList()))

		return navAnsatt
	}


}
class NavContext{
	val privatBrukere = PrivatBrukere()
	val navAnsatt = NavAnsatte()


	fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): Map<NorskIdent, Boolean> {
		val toMap = norskeIdenter.map { it to (privatBrukere.get(it)?.erSkjermet == true) }.toMap()
		return toMap
	}


}
