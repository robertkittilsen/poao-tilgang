package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.slf4j.LoggerFactory

class NavAnsattTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
	private val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilEksternBrukerPolicy {

	private val log = LoggerFactory.getLogger(javaClass)
	private val secureLog = LoggerFactory.getLogger("SecureLog")

	override val name = "NavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val harTilgangAbac = harTilgangAbac(input)

		try {
			val harTilgang = harTilgang(input)

			if (harTilgangAbac != harTilgang) {
				secureLog.info("ABAC=($harTilgangAbac) og POAO-tilgang=($harTilgang) har ulikt svar om tilgang. Input=$input")
			}
		} catch (e: Throwable) {
			log.error("Feil i POAO-tilgang implementasjon", e)
		}


		return harTilgangAbac
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, norskIdent)

		return if (harTilgang) Decision.Permit else Decision.Deny(
			"Deny fra ABAC",
			DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

	internal fun harTilgang(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input

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

		navAnsattTilgangTilOppfolgingPolicy.evaluate(
			NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
		).whenDeny { return it }

		return Decision.Permit
	}

}
