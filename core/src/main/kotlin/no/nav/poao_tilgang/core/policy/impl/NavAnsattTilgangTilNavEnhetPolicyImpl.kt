package no.nav.poao_tilgang.core.policy.impl

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.provider.ToggleProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import no.nav.poao_tilgang.core.utils.has
import org.slf4j.LoggerFactory
import java.time.Duration

class NavAnsattTilgangTilNavEnhetPolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val abacProvider: AbacProvider,
	private val meterRegistry: MeterRegistry,
	private val toggleProvider: ToggleProvider,
) : NavAnsattTilgangTilNavEnhetPolicy {

	private val modiaAdmin = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaAdmin
	private val modiaOppfolging = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhet"

	val secureLog = LoggerFactory.getLogger("SecureLog")

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		return if (toggleProvider.brukAbacDesision()) {
			val harTilgangAbac = harTilgangAbac(input)

			asyncLogDecisionDiff(name, input, ::harTilgang, { _ ->harTilgangAbac })

			harTilgangAbac
		} else {
			val resultat = harTilgang(input)

			asyncLogDecisionDiff(name, input, { _ -> resultat }, ::harTilgangAbac)
			resultat
		}
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val timer: Timer = meterRegistry.timer("app.poao-tilgang.NavAnsattTilgangTilNavEnhet")
		val startTime = System.currentTimeMillis();

		val harTilgangAbac = abacProvider.harVeilederTilgangTilNavEnhet(navIdent, input.navEnhetId)

		timer.record(Duration.ofMillis(System.currentTimeMillis() - startTime))

		return toAbacDecision(harTilgangAbac)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.has(modiaOppfolging)
			.whenDeny { return it }

		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId).has(modiaAdmin).whenPermit {
			secureLog.info("Tilgang gitt basert paa 0000-GA-Modia_Admin")
			return it
		}

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) return Decision.Permit else denyDecision
	}

}
