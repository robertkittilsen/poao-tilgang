package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import org.slf4j.LoggerFactory

class NavAnsattTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
	private val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy
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

	internal fun harTilgangAbac(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navIdent, norskIdent) = input

		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, norskIdent)

		return if (harTilgang) Decision.Permit else Decision.Deny(
			"Deny fra ABAC",
			DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

	internal fun harTilgang(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navIdent, norskIdent) = input

		navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
			NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
				navIdent = navIdent,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
			NavAnsattTilgangTilSkjermetPersonPolicy.Input(
				navIdent = navIdent,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navIdent = navIdent,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilOppfolgingPolicy.evaluate(
			NavAnsattTilgangTilOppfolgingPolicy.Input(navIdent)
		).whenDeny { return it }

		return Decision.Permit
	}

}
