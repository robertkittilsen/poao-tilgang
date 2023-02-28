package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import no.nav.poao_tilgang.core.utils.hasAtLeastOne
import org.slf4j.LoggerFactory

class NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
	private val oppfolgingsenhetProvider: OppfolgingsenhetProvider,
	private val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider,
	private val tilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicy,
	private val adGruppeProvider: AdGruppeProvider,
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider
) : NavAnsattTilgangTilEksternBrukerNavEnhetPolicy {

	private val secureLog = LoggerFactory.getLogger("SecureLog")
	private val nasjonalTilgangGrupper = adGruppeProvider.hentTilgjengeligeAdGrupper().let {
		listOf(
			it.gosysNasjonal,
			it.gosysUtvidbarTilNasjonal,
		)
	}

	override val name = "NavAnsattTilgangTilEksternBrukerNavEnhetPolicy"

	val denyDecision = Decision.Deny(
		message = "Brukeren har ikke oppfølgingsenhet eller geografisk enhet",
		reason = DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
	)

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input

		// Hvis man har nasjonal tilgang så trengs det ikke sjekk på enhet tilgang
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.hasAtLeastOne(nasjonalTilgangGrupper)
			.whenPermit { return it }

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)?.let { navEnhetId ->
			return harTilgangTilEnhetForBruker(navAnsattAzureId, navEnhetId, "geografiskEnhet")
		}
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)?.let { navEnhetId ->
			return harTilgangTilEnhetForBruker(navAnsattAzureId, navEnhetId, "oppfolgingsEnhet")
		}

		secureLog.info("Returnerer Deny i $name")

		return denyDecision
	}

	fun harTilgangTilEnhetForBruker(navAnsattAzureId: AzureObjectId, navEnhetId: NavEnhetId, typeEnhet: String): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { navEnhetId == it.enhetId }
		secureLog.info("$name, harTilgangTilEnhet: $harTilgangTilEnhet, navEnhetForBruker: $navEnhetId for type Enhet: $typeEnhet")
		return if (harTilgangTilEnhet) Decision.Permit else denyDecision
	}
}
