package no.nav.poao_tilgang.application.utils

import com.github.benmanes.caffeine.cache.Caffeine
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNotNull
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstCacheNull
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class CacheUtilsTest {

	@Test
	fun `tryCacheFirstNotNull skal cache for samme key`() {
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
	fun `tryCacheFirstNotNull skal ikke cache for forskjellig keys`() {
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
	fun `tryCacheFirstCacheNull skal cache null verdier`() {
		val cache = Caffeine.newBuilder()
			.maximumSize(5)
			.build<String, NullWrapper<String>>()

		val counter = AtomicInteger()
		val supplier = {
			counter.incrementAndGet()
			null
		}

		tryCacheFirstCacheNull(cache, "key1", supplier)
		tryCacheFirstCacheNull(cache, "key1", supplier)

		counter.get() shouldBe 1
	}

	@Test
	fun `tryCacheFirstNullable skal ikke cache null`() {
		val cache = Caffeine.newBuilder()
			.maximumSize(5)
			.build<String, String>()

		val counter = AtomicInteger()
		val supplier = {
			counter.incrementAndGet()
			null
		}

		CacheUtils.tryCacheFirstNullable(cache, "key1", supplier)
		CacheUtils.tryCacheFirstNullable(cache, "key1", supplier)

		counter.get() shouldBe 2
	}

}
