package no.nav.poao_tilgang.core.policy.test_utils

import no.nav.poao_tilgang.core.utils.Timer
import java.time.Duration

class MockTimer: Timer {
	override fun record(name: String, duration: Duration, vararg tags: String) {

	}

	override fun <T> measure(name: String, vararg tags: String, method: () -> T): T {
		return method()
	}
}
