package no.nav.poao_tilgang.client

import java.util.*

sealed class PolicyInput

data class NavAnsattTilgangTilEksternBrukerPolicyInputV1(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattTilgangTilEksternBrukerPolicyInputV2(
	val navAnsattAzureId: UUID,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattTilgangTilModiaPolicyInputV1(
	val navAnsattAzureId: UUID
) : PolicyInput()
