package no.nav.poao_tilgang.application.utils

import com.github.benmanes.caffeine.cache.Cache

object CacheUtils {

	fun <K : Any, V: Any> tryCacheFirstCacheNull(cache: Cache<K, NullWrapper<V>>, key: K, valueSupplier: () -> V?): V? {
		val value = cache.getIfPresent(key)

		if (value == null) {
			val newValue = valueSupplier.invoke()
			cache.put(key, NullWrapper(newValue))
			return newValue
		}

		return value.data
	}

	fun <K: Any, V: Any> tryCacheFirstNotNull(cache: Cache<K, V>, key: K, valueSupplier: () -> V): V {
		val value = cache.getIfPresent(key)

		if (value == null) {
			val newValue = valueSupplier.invoke()
			cache.put(key, newValue)
			return newValue
		}

		return value
	}

	fun <K : Any, V: Any> tryCacheFirstNullable(cache: Cache<K, V>, key: K, valueSupplier: () -> V?): V? {
		val value = cache.getIfPresent(key)

		if (value == null) {
			val newValue: V = valueSupplier.invoke() ?: return null
			cache.put(key, newValue)
			return newValue
		}

		return value
	}
}

data class NullWrapper<D>(
	val data: D?
)
