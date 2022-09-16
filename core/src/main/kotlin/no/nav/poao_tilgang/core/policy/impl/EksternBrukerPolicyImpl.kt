package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.policy.EksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.FortroligBrukerPolicy
import no.nav.poao_tilgang.core.policy.StrengtFortroligBrukerPolicy
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider

class EksternBrukerPolicyImpl(
	private val diskresjonskodeProvider: DiskresjonskodeProvider,
	private val fortroligBrukerPolicy: FortroligBrukerPolicy,
	private val strengtFortroligBrukerPolicy: StrengtFortroligBrukerPolicy
) : EksternBrukerPolicy {

	override val name = "HarNavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: EksternBrukerPolicy.Input): Decision {
		val (navIdent, norskIdent) = input
		val diskresjonskode = diskresjonskodeProvider.hentDiskresjonskode(norskIdent)

		if (diskresjonskode == Diskresjonskode.STRENGT_FORTROLIG) {
			strengtFortroligBrukerPolicy.evaluate(
				StrengtFortroligBrukerPolicy.Input(navIdent)
			).let {
				if (it is Decision.Deny) return it
			}
		} else if (diskresjonskode == Diskresjonskode.FORTROLIG) {
			fortroligBrukerPolicy.evaluate(
				FortroligBrukerPolicy.Input(navIdent)
			).let {
				if (it is Decision.Deny) return it
			}
		}

		return Decision.Deny(
			message = "Policy er ikke implementert",
			reason = DecisionDenyReason.POLICY_NOT_IMPLEMENTED
		)
	}

}
