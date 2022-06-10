package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyType
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider

class TilgangTilEksternBrukerPolicy(
	private val diskresjonskodeProvider: DiskresjonskodeProvider,
	private val tilgangTilFortroligBrukerPolicy: TilgangTilFortroligBrukerPolicy,
	private val tilgangTilStrengtFortroligBrukerPolicy: TilgangTilStrengtFortroligBrukerPolicy
) : Policy<TilgangTilEksternBrukerPolicy.Input>(PolicyType.TILGANG_TIL_EKSTERN_PERSON) {

	data class Input(
		val navIdent: String,
		val norskIdent: String
	)

	override fun harTilgang(input: Input): Decision {
		val (navIdent, norskIdent) = input
		val diskresjonskode = diskresjonskodeProvider.hentDiskresjonskode(norskIdent)

		if (diskresjonskode == Diskresjonskode.STRENGT_FORTROLIG) {
			tilgangTilStrengtFortroligBrukerPolicy.harTilgang(navIdent).let {
				if (it is Decision.Deny) return it
			}
		} else if (diskresjonskode == Diskresjonskode.FORTROLIG) {
			tilgangTilFortroligBrukerPolicy.harTilgang(navIdent).let {
				if (it is Decision.Deny) return it
			}
		}

		return Decision.Permit
	}

}
