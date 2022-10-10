package no.nav.poao_tilgang.application.middleware

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.MDC
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class RequesterNameLogFilter(
	private val tokenValidationContextHolder: TokenValidationContextHolder
) : Filter {

	private val requesterLabelName = "requester"

	override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
		val azpName: String? = tokenValidationContextHolder
			.tokenValidationContext
			.anyValidClaims
			.map { claims -> claims.getStringClaim("azp_name") }
			.orElse(null)

		MDC.remove(requesterLabelName)
		azpName?.let { MDC.put(requesterLabelName, azpName) }
		chain.doFilter(req, res)
	}

}
