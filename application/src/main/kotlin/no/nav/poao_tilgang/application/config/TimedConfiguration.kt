package no.nav.poao_tilgang.application.config

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
open class TimedConfiguration {
	@Bean
	open fun timedAspect(registry: MeterRegistry): TimedAspect {
		return TimedAspect(registry)
	}
}
