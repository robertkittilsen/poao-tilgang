package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.policy.*

class PolicyResolver(
	private val navAnsattTilgangTilEksternBrukerPolicy: NavAnsattTilgangTilEksternBrukerPolicy,
	private val navAnsattTilgangTilModiaPolicy: NavAnsattTilgangTilModiaPolicy,
	private val eksternBrukerTilgangTilEksternBrukerPolicy: EksternBrukerTilgangTilEksternBrukerPolicy,
	private val navAnsattTilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicy,
	private val navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
	private val navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
	private val navAnsattTiltangTilEnhetMedSperrePolicy: NavAnsattTilgangTilNavEnhetMedSperrePolicy,
	private val navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy
) {
	fun evaluate(input: PolicyInput): PolicyResult {
		return when (input) {
			is NavAnsattTilgangTilEksternBrukerPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilEksternBrukerPolicy)
			is NavAnsattTilgangTilModiaPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilModiaPolicy)
			is EksternBrukerTilgangTilEksternBrukerPolicy.Input -> evaluateWithName(input, eksternBrukerTilgangTilEksternBrukerPolicy)
			is NavAnsattTilgangTilNavEnhetPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilNavEnhetPolicy)
			is NavAnsattBehandleFortroligBrukerePolicy.Input -> evaluateWithName(input, navAnsattBehandleFortroligBrukerePolicy)
			is NavAnsattBehandleStrengtFortroligBrukerePolicy.Input -> evaluateWithName(input, navAnsattBehandleStrengtFortroligBrukerePolicy)
			is NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input -> evaluateWithName(input, navAnsattTiltangTilEnhetMedSperrePolicy)
			is NavAnsattBehandleSkjermedePersonerPolicy.Input -> evaluateWithName(input, navAnsattBehandleSkjermedePersonerPolicy)
			else -> throw PolicyNotImplementedException("Håndtering av policy ${input.javaClass.canonicalName} er ikke implementert")
		}
	}


	private fun <I : PolicyInput> evaluateWithName(input: I, policy: Policy<I>): PolicyResult {
		var decision = policy.evaluate(input)

		return PolicyResult(policy.name, decision)
	}
}
