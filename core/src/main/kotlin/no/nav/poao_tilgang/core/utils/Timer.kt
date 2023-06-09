package no.nav.poao_tilgang.core.utils

import java.time.Duration

interface Timer {
	fun record(name: String, duration: Duration, vararg tags: String)

	fun <T> measure(name: String, vararg tags: String, method: () -> T): T
}
