package no.nav.poao_tilgang.core.policy.impl

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetMedSperrePolicy
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.provider.ToggleProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import no.nav.poao_tilgang.core.utils.has
import java.time.Duration

class NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val abacProvider: AbacProvider,
	private val meterRegistry: MeterRegistry,
	private val toggleProvider: ToggleProvider,
) : NavAnsattTilgangTilNavEnhetMedSperrePolicy {

	private val aktivitetsplanKvp = adGruppeProvider.hentTilgjengeligeAdGrupper().aktivitetsplanKvp

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet med sperre",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhetMedSperre"

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {

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

	private fun harTilgangAbac(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val timer: Timer = meterRegistry.timer("app.poao-tilgang.NavAnsattTilgangTilNavEnhetMedSperre")
		val startTime=System.currentTimeMillis();

		val harTilgangAbac = abacProvider.harVeilederTilgangTilNavEnhetMedSperre(navIdent, input.navEnhetId)

		timer.record(Duration.ofMillis(System.currentTimeMillis()-startTime))

		return toAbacDecision(harTilgangAbac)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.has(aktivitetsplanKvp)
			.whenPermit { return it }

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) Decision.Permit else denyDecision
	}

}
