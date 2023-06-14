package no.nav.poao_tilgang.application.service

import io.micrometer.core.instrument.MeterRegistry
import no.nav.poao_tilgang.core.utils.Timer
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TimerService(
	private val meterRegistry: MeterRegistry
): Timer {
	override fun record(name: String, duration: Duration, vararg tags: String) {
		val timer = meterRegistry.timer(name, *tags)
		timer.record(duration)
	}

	override fun <T> measure(name: String, vararg tags: String, method: () -> T): T {
		val timer = meterRegistry.timer(name, *tags)
		return timer.recordCallable(method)!!
	}

}
