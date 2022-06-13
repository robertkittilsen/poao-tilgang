package no.nav.poao_tilgang.provider_impl

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.client.skjermet_person.SkjermetPersonClient
import no.nav.poao_tilgang.utils.SecureLog.secureLog
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class SkjermetPersonService(
	private val skjermetPersonClient: SkjermetPersonClient
) {

	private val norkIdentToErSkjermetCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
		.build<String, Boolean>()

	fun erSkjermetPerson(norskIdent: String): Boolean {
		return erSkjermetWithCache(listOf(norskIdent))
			.getOrElse(norskIdent) {
				secureLog.warn("Mangler data for skjermet person med fnr=$norskIdent, defaulter til true")
				return@getOrElse true
			}
	}

	fun erSkjermetPerson(norskeIdenter: List<String>): Map<String, Boolean> {
		return erSkjermetWithCache(norskeIdenter)
	}

	private fun erSkjermetWithCache(norskeIdenter: List<String>): Map<String, Boolean> {
		val cachetSkjerming = mutableMapOf<String, Boolean>()
		val manglendeSkjerming: MutableList<String> = mutableListOf()

		norskeIdenter.forEach {
			val erSkjermet = norkIdentToErSkjermetCache.getIfPresent(it)

			if (erSkjermet != null) {
				cachetSkjerming[it] = erSkjermet
			} else {
				manglendeSkjerming.add(it)
			}
		}

		if (manglendeSkjerming.isEmpty()) {
			return cachetSkjerming
		}

		val skjerming = skjermetPersonClient.erSkjermet(manglendeSkjerming)

		skjerming.forEach {
 			norkIdentToErSkjermetCache.put(it.key, it.value)
		}

		cachetSkjerming.putAll(skjerming)

		return cachetSkjerming
	}

}
