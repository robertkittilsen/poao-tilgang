package no.nav.poao_tilgang.application.middleware


import no.nav.poao_tilgang.core.domain.PolicyNotImplementedException
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
open class ControllerAdvice {

	private val log = LoggerFactory.getLogger(javaClass)

	@ExceptionHandler(PolicyNotImplementedException::class)
	fun handlePolicyNotImplementedException(e: PolicyNotImplementedException): ResponseEntity<String> {
		log.error(e.message, e)

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(HttpStatus.BAD_REQUEST.reasonPhrase)
	}

	@ExceptionHandler(JwtTokenUnauthorizedException::class)
	fun handleJwtTokenUnauthorizedException(e: JwtTokenUnauthorizedException): ResponseEntity<String> {
		log.error("Received an unauthenticated request", e)

		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(HttpStatus.UNAUTHORIZED.reasonPhrase)
	}

}
