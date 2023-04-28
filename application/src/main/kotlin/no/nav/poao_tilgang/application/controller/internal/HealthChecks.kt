package no.nav.poao_tilgang.application.controller.internal

import no.nav.common.abac.AbacClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class HealthChecksPoaoTilgang(
	private val abacClient: AbacClient
): HealthIndicator {
	private val logger: Logger = LoggerFactory.getLogger(this::class.java)
	override fun health(): Health {
		logger.debug("Starter healthcheck for abacClient")
		if (abacClient.checkHealth().isHealthy){
			logger.debug("Healthcheck for abacClient: READY")
			return Health.up().build()
		}
		logger.warn("Healthcheck for abacClient: NOT READY")
		return Health.down().build()
	}
}
