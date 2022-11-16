package no.nav.poao_tilgang.client.utils

import com.github.benmanes.caffeine.cache.Cache

object CacheUtils {

	inline fun <K: Any, V: Any> tryCacheFirstNotNull(cache: Cache<K, V>, key: K, valueSupplier: () -> V): V {
		val value = cache.getIfPresent(key)

		if (value == null) {
			val newValue = valueSupplier.invoke()
			cache.put(key, newValue)
			return newValue
		}

		return value
	}
}
