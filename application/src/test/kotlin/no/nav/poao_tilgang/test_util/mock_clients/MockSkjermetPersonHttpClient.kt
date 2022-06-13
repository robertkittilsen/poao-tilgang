package no.nav.poao_tilgang.test_util.mock_clients

import no.nav.poao_tilgang.test_util.MockHttpClient
import no.nav.poao_tilgang.utils.JsonUtils.toJsonString

class MockSkjermetPersonHttpClient : MockHttpClient() {

	fun enqueueErSkjermet(skjerming: Map<String, Boolean>) {
		enqueue(
			body = toJsonString(skjerming)
		)
	}

}
