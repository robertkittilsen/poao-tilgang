package no.nav.poao_tilgang.client

sealed class PolicyInput

data class NavAnsattTilgangTilEksternBrukerPolicyInput(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattTilgangTilModiaPolicyInput(
	val navIdent: String
) : PolicyInput()
