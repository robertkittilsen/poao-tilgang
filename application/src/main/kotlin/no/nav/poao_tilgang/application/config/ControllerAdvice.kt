package no.nav.poao_tilgang.application.config

import no.nav.poao_tilgang.application.exception.InvalidPolicyRequestException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
open class ControllerAdvice {

	private val log = LoggerFactory.getLogger(javaClass)

	@ExceptionHandler(InvalidPolicyRequestException::class)
	fun handleInvalidPolicyRequestException(e: InvalidPolicyRequestException): ResponseEntity<String> {
		log.info(e.message, e)

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(HttpStatus.BAD_REQUEST.reasonPhrase)
	}

}
