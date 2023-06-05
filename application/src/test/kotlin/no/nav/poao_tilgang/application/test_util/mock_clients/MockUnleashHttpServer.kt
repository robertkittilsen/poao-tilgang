package no.nav.poao_tilgang.application.test_util.mock_clients


import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
class MockUnleashHttpServer: MockHttpServer() {

	init {
		mockTogles()
		mockRegister()
	}
	fun mockTogles() {
		val response = MockResponse()
			.setBody(
				responseBody
			)

		handleRequest(
			matchPath = "/api/client/features",
			matchMethod = "GET",
			response = response
		)
	}

	fun mockRegister() {
		val response = MockResponse()
			.setBody(responseBody)

		handleRequest(
			matchPath = "/api/client/register",
			matchMethod = "POST",
			response = response
		)
	}

	//language=JSON
	val  responseBody = """
					{
					  "version": 1,
					  "features": [
					    {
					      "name": "Feature.A",
					      "type": "release",
					      "enabled": false,
					      "stale": false,
					      "strategies": [
					        {
					          "name": "default",
					          "parameters": {}
					        }
					      ],
					      "strategy": "default",
					      "parameters": {}
					    },
					    {
					      "name": "Feature.B",
					      "type": "killswitch",
					      "enabled": true,
					      "stale": false,
					      "strategies": [
					        {
					          "name": "ActiveForUserWithId",
					          "parameters": {
					            "userIdList": "123,221,998"
					          }
					        },
					        {
					          "name": "GradualRolloutRandom",
					          "parameters": {
					            "percentage": "10"
					          }
					        }
					      ],
					      "strategy": "ActiveForUserWithId",
					      "parameters": {
					        "userIdList": "123,221,998"
					      }
					    }
					  ]
					}
				""".trimIndent()
}
