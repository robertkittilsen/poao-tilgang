package no.nav.poao_tilgang.application.config

import io.micrometer.core.aop.TimedAspect
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
open class ApplicationConfigTest {
	@Bean
	open fun timedAspect(): TimedAspect? {
		val appMicrometerRegistry = Mockito.mock(PrometheusMeterRegistry::class.java)
		return TimedAspect(appMicrometerRegistry)
	}

}
