package no.nav.poao_tilgang.application.controller.internal

import no.nav.common.abac.AbacClient
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class HealthChecksPoaoTilgang(
	private val abacClient: AbacClient
): HealthIndicator {
	override fun health(): Health {
		if (abacClient.checkHealth().isHealthy){
			return Health.up().build();
		}
		return Health.down().build();
	}
}
