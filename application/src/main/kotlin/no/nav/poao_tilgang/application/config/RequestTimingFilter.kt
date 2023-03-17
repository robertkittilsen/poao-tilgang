package no.nav.poao_tilgang.application.config

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.io.IOException
import javax.annotation.PreDestroy
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
@Component
class RequestTimingFilter : Filter {
	private val log = LoggerFactory.getLogger(javaClass)
	private val xRequestTime = "x_request_time_ms"
	@Throws(IOException::class, ServletException::class)
	override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
		val startTime = System.currentTimeMillis()
		try {
			log.debug("Request received: {} {}", (request as HttpServletRequest).method, (request as HttpServletRequest).requestURI)
			chain.doFilter(request, response)
		} finally {
			val duration = System.currentTimeMillis() - startTime
			if (duration > 250) {
				val httpRequest = request as HttpServletRequest
				MDC.put(xRequestTime, duration.toString())
				log.debug("Slow request detected: {} {} ({}ms)", httpRequest.method, httpRequest.requestURI, duration)
				MDC.remove(xRequestTime)
			}
		}
	}
}
