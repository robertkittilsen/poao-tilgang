package no.nav.poao_tilgang.test_util

import no.nav.security.mock.oauth2.MockOAuth2Server
import java.util.concurrent.atomic.AtomicBoolean

open class MockOAuthServer {

	private val azureAdIssuer = "azuread"

	companion object {
		private val server = MockOAuth2Server()
		private var isStarted = AtomicBoolean(false)
	}

	init {
		if (!isStarted.get()) {
			isStarted.set(true)

			server.start()
		}

		System.setProperty("MOCK_AZURE_AD_DISCOVERY_URL", server.wellKnownUrl(azureAdIssuer).toString())
	}

	fun shutdownMockServer() {
		server.shutdown()
	}

	fun azureAdToken(
		subject: String = "test",
		audience: String = "test",
		claims: Map<String, Any> = emptyMap()
	): String {
		return server.issueToken(azureAdIssuer, subject, audience, claims).serialize()
	}

}
