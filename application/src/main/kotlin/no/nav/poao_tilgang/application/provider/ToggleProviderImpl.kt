package no.nav.poao_tilgang.application.provider

import no.nav.common.featuretoggle.UnleashClient
import no.nav.poao_tilgang.core.provider.ToggleProvider
import org.springframework.stereotype.Component

@Component
class ToggleProviderImpl(private val unleashClient: UnleashClient) : ToggleProvider {
	override fun brukAbacDesision(): Boolean {
		return !unleashClient.isEnabled("poao-tilgang.use-poao-tilgang-decision")
	}
}
