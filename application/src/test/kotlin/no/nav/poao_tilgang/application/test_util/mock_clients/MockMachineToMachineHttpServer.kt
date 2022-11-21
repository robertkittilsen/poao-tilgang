package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockMachineToMachineHttpServer : MockHttpServer() {

	companion object {
		const val tokenPath = "/token"

		// Dette er en test-generert JWK
		const val jwk = """
			{"p":"-mt2h4bf5GvXcROH3H5WqXadDqoHhKou2ku5ILzzeMMOb0muQZEfGHl4Tvw_EfweM_LOOLeZIOSockV2UwbEXWd7xeYqF4O9R2Pg6dpG8-_oj06Zq3wz37EWqclwGUIwXrcCSozquTCTNNg6hdgIRMpRHWFU-oLXwnO6VRTbD3E","kty":"RSA","q":"uT3kmHbkApnH9T7fJ_kQ9SSCE9ise7KfZ-lTkxiiDeZp01gAkzFTIvRZN7DR9sTN2usteE5alQhXONig-UyW5u5qNtXPhu6bgbT8iTbqbKQZ5n_YPEPQtk0rvBP23jQ8gQHJVbZ9DNhBJA12tj46aPq0Vd_JqR1wyPGfbGsfPuk","d":"QVtg0o94A-ljMtzHyJEM-KWf1XsRzvxogkxQS49RSfRxwchd1hwfbY-4ZPNGXxJaQLWLJRsrCe-y6TFVDv0VnW9YyN-tmvwuRX1uK25J3KAm3YAYgLSWNbS6cijOBIy9K5r15DrWvCV2w3W5-Lb5yS1nISeskhDAkPDZAs6tfbdfSJl-Ei30rVR7WLWZqau0TCF0Q9aSW7ajaGfi3qhto2t316mf1Roj7Gesd8JQGM-F6zjl7TAOvfvQJ3rVPI-HNOi46amhzD9s8oTTZjtrYH7o1xuxoDTEdYqbPSlE7xBA4YRCuhO9fyPrmxlFYZRDL986ZqckSWGnDgEhqITpAQ","e":"AQAB","use":"sig","kid":"7dadb0af-0948-42b8-89c7-b32d616e6609","qi":"vbm7ZvpU9mO2ZtzWU-LpvvWxAeBpD41jRtzFZQwmi1uuRp-Ce8Bzp8p8CIkxU_anBvMukThVf5wo-TA9M53ItuFxbOy86b6ng0F0jge1wrqeic4U7zNzecgc56506OUhuB_twNK_gASYLC11daAUh7-N90bmS6i8vHISRhO7rgI","dp":"B94WdD2VjSyiMShG7XN-r4ZNeud2owFhxXP5lgC5uYKPUonDEsZHbtJqKDkelicQn9syBXAnPHRSonxyMjuVMcYNinMnwWNfL_4AU_iKoCFach4rD8swKgf7SUxN3E1LDb68MLFqsnTHOzLL6-aUo-yjTcQAEnrH4pEWOpQUKdE","dq":"atkihDb_qeheCD3tpjbp6xzV0vDF_Mu5WsyhIlWxE-d13ywBmOV4mM7sr0zUr0wxmTbSEXTQluoqtWVu5J2i5S1cIqkNfT0ggZ6Nk_ATI_s73VKcvZY4Sw8UTPYPDXh_9cG8Ci6qzr_mQw9pDBG0y1zgrC_bOCr9JUsJtBXN_hE","n":"tTQz4AhuluYmYmEyEij_WNAHOYnx8XftGTMUrxh2n8iA_Oz06kvTqJIjyiZJoFBUa7EGoo_eF6Obgc-t3l0O3UoUZ_kjkq3xl66ZDyU0TBeEbPtoeZx7ZoFmElwOnRCs2dCUqm3ZN7CrTC8Ejaq611XXkRZv0_Uyz6YAhB90H67XYO7lorTsRxH3iy8CNLTm-GihsM4EaoFxTrWI3d9NprAG4j7of-2NyU5cwfoYOyVFJ76UTY9WVykbBv2NV6zWDB-fZ8RJPBoHFmo9JSaQjeIHJT4UrAIftgjglo7HYYO5gtS502gHZHH0KtzBQxfHTunhQ7E5Cu_O3PskIs1r2Q"}
		"""
	}

	init {
		mockToken()
	}

	private fun mockToken() {
		val predicate = { req: RecordedRequest ->
			req.path == tokenPath
		}

		// Ikke ekte tokens
		val body = """
			{
			  "token_type" : "Bearer",
			  "id_token" : "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
			  "access_token" : "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
			  "refresh_token" : "ccf818e6-9114-45cf-819f-c030f153cf13",
			  "expires_in" : 31535999,
			  "scope" : "openid somescope"
			}
		""".trimIndent()

		val response = MockResponse().setResponseCode(200).setBody(body).setHeader("content-type", "application/json")

		addResponseHandler(predicate, response)
	}
}
