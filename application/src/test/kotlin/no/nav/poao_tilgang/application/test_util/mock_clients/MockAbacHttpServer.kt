package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.TilgangType
import okhttp3.mockwebserver.MockResponse

class MockAbacHttpServer : MockHttpServer() {

	fun mockPermit(tilgangType: TilgangType) {
		handleRequest(
			matchPath = "/",
			matchMethod = "POST",
			matchBodyContains = matchAbacTilgangAction(tilgangType),
			response = MockResponse()
				.setBody(
					"""
						{
						  "Response": {
						    "Decision": "Permit",
						    "Status": {
						      "StatusCode": {
						        "Value": "urn:oasis:names:tc:xacml:1.0:status:ok",
						        "StatusCode": {
						          "Value": "urn:oasis:names:tc:xacml:1.0:status:ok"
						        }
						      }
						    }
						  }
						}
					""".trimIndent()
				)
		)
	}

	fun mockDeny(tilgangType: TilgangType) {
		handleRequest(
			matchPath = "/",
			matchMethod = "POST",
			matchBodyContains = matchAbacTilgangAction(tilgangType),
			response = MockResponse()
				.setBody(
					"""
						{
						  "Response": {
						    "Decision": "Deny",
						    "Status": {
						      "StatusCode": {
						        "Value": "urn:oasis:names:tc:xacml:1.0:status:ok",
						        "StatusCode": {
						          "Value": "urn:oasis:names:tc:xacml:1.0:status:ok"
						        }
						      }
						    },
						    "AssociatedAdvice": {
						      "Id": "no.nav.abac.advices.reason.deny_reason",
						      "AttributeAssignment": [
						        {
						          "AttributeId": "no.nav.abac.advice.fritekst",
						          "Value": "Ikke tilgang",
						          "Category": "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        },
						        {
						          "AttributeId": "no.nav.abac.attributter.adviceorobligation.cause",
						          "Value": "cause",
						          "Category": "urn:oasis:names:tc:xacml:3.0:attribute-category:environment",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        },
						        {
						          "AttributeId": "no.nav.abac.attributter.adviceorobligation.deny_policy",
						          "Value": "deny_policy",
						          "Category": "urn:oasis:names:tc:xacml:3.0:attribute-category:environment",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        },
						        {
						          "AttributeId": "no.nav.abac.attributter.adviceorobligation.deny_rule",
						          "Value": "deny_rule",
						          "Category": "urn:oasis:names:tc:xacml:3.0:attribute-category:environment",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        }
						      ]
						    }
						  }
						}
					""".trimIndent()
				)
		)
	}

	private fun matchAbacTilgangAction(tilgangType: TilgangType): String {
		val action = when(tilgangType) {
			TilgangType.LESE -> "read"
			TilgangType.SKRIVE -> "update"
		}
		return """"Value": "$action""""
	}
}
