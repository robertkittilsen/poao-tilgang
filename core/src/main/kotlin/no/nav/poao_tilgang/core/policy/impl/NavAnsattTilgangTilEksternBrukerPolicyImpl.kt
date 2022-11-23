package no.nav.poao_tilgang.core.policy.impl

import kotlinx.coroutines.slf4j.MDCContext
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.slf4j.LoggerFactory

import kotlinx.coroutines.*

class NavAnsattTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
	private val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
	private val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilEksternBrukerPolicy {

	private val log = LoggerFactory.getLogger(javaClass)
	private val secureLog = LoggerFactory.getLogger("SecureLog")

	override val name = "NavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val harTilgangAbac = harTilgangAbac(input)

		asyncLogDecisionDiff(input, harTilgangAbac)

		return harTilgangAbac
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, tilgangType, norskIdent)

		return if (harTilgang) Decision.Permit else Decision.Deny(
			"Deny fra ABAC",
			DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

	@OptIn(DelicateCoroutinesApi::class)
	private fun asyncLogDecisionDiff(input: NavAnsattTilgangTilEksternBrukerPolicy.Input, harTilgangAbac: Decision) {
		GlobalScope.launch(MDCContext()) {
			try {
				val harTilgang = harTilgang(input)

				if (harTilgangAbac != harTilgang) {
					secureLog.info("Decision diff - ulikt svar: ABAC=($harTilgangAbac) POAO-tilgang=($harTilgang) Input=$input")
				} else {
					secureLog.info("Decision diff - likt svar: ABAC=($harTilgangAbac) POAO-tilgang=($harTilgang) Input=$input")
				}
			} catch (e: Throwable) {
				log.error("Feil i POAO-tilgang implementasjon", e)
			}
		}
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input

		when (tilgangType) {
			TilgangType.LESE ->
				navAnsattTilgangTilModiaGenerellPolicy.evaluate(
					NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }

			TilgangType.SKRIVE ->
				navAnsattTilgangTilOppfolgingPolicy.evaluate(
					NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }
		}

		navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
			NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
			NavAnsattTilgangTilSkjermetPersonPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		return Decision.Permit
	}

}
