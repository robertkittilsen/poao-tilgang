package no.nav.poao_tilgang.client.api

class ApiResult<R>(
	private val throwable: ApiException?,
	private val result: R?
) {

	init {
		assert((result == null) xor (throwable == null))
			{ "Invalid result field combination" }
	}

	companion object {
		fun <R> success(result: R): ApiResult<R> {
			return ApiResult(null, result)
		}

		fun <R> failure(exception: ApiException): ApiResult<R> {
			return ApiResult(exception, null)
		}
	}

	val isSuccess: Boolean get() = result != null

	val isFailure: Boolean get() = throwable != null

	val exception: ApiException? get() = throwable

	// ?
	fun get(): R? = result

	fun getOrDefault(defaultValue: R): R = result ?: defaultValue

	fun getOrDefault(defaultValueProvider: () -> R): R = result ?: defaultValueProvider()

	fun getOrThrow(): R {
		return result ?: throw (throwable ?: IllegalStateException("Unknown error"))
	}

	fun <T> map(transform: (value: R) -> T): ApiResult<T> {
		result?.let {
			return success(transform(it))
		}

		@Suppress("UNCHECKED_CAST")
		return this as ApiResult<T>
	}

	fun <T> flatMap(transform: (value: R) -> ApiResult<T>): ApiResult<T> {
		result?.let {
			return transform(it)
		}

		@Suppress("UNCHECKED_CAST")
		return this as ApiResult<T>
	}

}
