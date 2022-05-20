package no.nav.poao_tilgang.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object RestUtils {

	private val mediaTypeJson = "application/json".toMediaType()

	fun String.toJsonRequestBody(): RequestBody {
		return this.toRequestBody(mediaTypeJson)
	}

}
