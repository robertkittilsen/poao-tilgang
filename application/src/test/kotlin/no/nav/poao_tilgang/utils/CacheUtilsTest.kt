package no.nav.poao_tilgang.utils

import com.github.benmanes.caffeine.cache.Caffeine
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.utils.CacheUtils.tryCacheFirstNotNull
import no.nav.poao_tilgang.utils.CacheUtils.tryCacheFirstNullable
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class CacheUtilsTest {

	@Test
	fun `skal cache for samme key`() {
		val cache = Caffeine.newBuilder()
			.maximumSize(5)
			.build<String, String>()

		val counter = AtomicInteger()
		val supplier = {
			counter.incrementAndGet()
			"value"
		}

		tryCacheFirstNotNull(cache, "key1", supplier)
		tryCacheFirstNotNull(cache, "key1", supplier)

		counter.get() shouldBe 1
	}

	@Test
	fun `skal ikke cache for forskjellig keys`() {
		val cache = Caffeine.newBuilder()
			.maximumSize(5)
			.build<String, String>()

		val counter = AtomicInteger()
		val supplier = {
			counter.incrementAndGet()
			"value"
		}

		tryCacheFirstNotNull(cache, "key1", supplier)
		tryCacheFirstNotNull(cache, "key2", supplier)

		counter.get() shouldBe 2
	}

	@Test
	fun `skal ikke cache null`() {
		val cache = Caffeine.newBuilder()
			.maximumSize(5)
			.build<String, String>()

		val counter = AtomicInteger()
		val supplier = {
			counter.incrementAndGet()
			null
		}

		tryCacheFirstNullable(cache, "key1", supplier)
		tryCacheFirstNullable(cache, "key1", supplier)

		counter.get() shouldBe 2
	}

}
