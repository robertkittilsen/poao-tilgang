package no.nav.poao_tilgang.api.dto.request.policy_input

data class EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto(
	val rekvirentNorskIdent: String, // Den som ber om tilgang
	val ressursNorskIdent: String // Den som bes tilgang om
)
