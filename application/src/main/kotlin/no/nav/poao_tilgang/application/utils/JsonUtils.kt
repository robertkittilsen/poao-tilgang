package no.nav.poao_tilgang.application.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue

object JsonUtils {

	//TODO burde denne flyttes til api-core-mapper? da har vi bare en instans av objectmapper
	//eksisiterer også en instangs av objectmapper i api-core-mapper
	val objectMapper: ObjectMapper = ObjectMapper()
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	inline fun <reified T> fromJsonString(jsonStr: String): T {
		return objectMapper.readValue(jsonStr)
	}

	fun toJsonString(any: Any): String {
		return objectMapper.writeValueAsString(any)
	}

}
