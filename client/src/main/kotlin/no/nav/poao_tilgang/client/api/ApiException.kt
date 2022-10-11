package no.nav.poao_tilgang.client.api

sealed class ApiException(
	message: String,
	cause: Throwable? = null
) : Exception(message, cause)

class BadHttpStatusApiException(
	val httpStatus: Int,
	val responseBody: String? = null
) : ApiException("Received response with bad HTTP status. status=$httpStatus response=$responseBody")

class ResponseDataApiException(
	message: String
) : ApiException(message) {
	companion object {
		fun missingBody() = ResponseDataApiException("Body is missing from response")
	}
}

class NetworkApiException(
	override val cause: Throwable
) : ApiException("HTTP request failed: ${cause.message}", cause)

class UnspecifiedApiException(
	override val cause: Throwable
) : ApiException("Unspecified exception: ${cause.message}", cause)
