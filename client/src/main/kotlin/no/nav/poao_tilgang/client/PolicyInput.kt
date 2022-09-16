package no.nav.poao_tilgang.client

sealed class PolicyInput

data class EksternBrukerPolicyInput(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class FortroligBrukerPolicyInput(
	val navIdent: String
) : PolicyInput()

data class ModiaPolicyInput(
	val navIdent: String
) : PolicyInput()

data class SkjermetPersonPolicyInput(
	val navIdent: String
) : PolicyInput()

data class StrengtFortroligBrukerPolicyInput(
	val navIdent: String
) : PolicyInput()

