package no.nav.poao_tilgang.client.api

sealed class ApiException(
	message: String,
	cause: Throwable? = null
) : Exception(message, cause)

class BadHttpStatusApiException(
	val httpStatus: Int,
	val responseBody: String? = null
) : ApiException("Received response with bad HTTP status. status=$httpStatus response=$responseBody")

class MalformedResponseApiException(
	message: String
) : ApiException(message) {
	companion object {
		fun missingBody() = MalformedResponseApiException("Body is missing from response")
	}
}

class ResponseDataApiException(
	message: String
) : ApiException(message)

class NetworkApiException(
	override val cause: java.lang.Exception
) : ApiException("HTTP request failed: ${cause.message}", cause)
