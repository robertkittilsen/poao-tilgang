package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.poao_tilgang.poao_tilgang_test_core.NavContext
import no.nav.poao_tilgang.poao_tilgang_test_core.Policies


class PoaoTilgangWiremock(portnummer: Int = 0, baspath: String= "", navContext: NavContext = NavContext()) {
	val mocks = WiremockTransformers(policies = Policies(navContext), baspath = baspath)
	val wireMockServer = WireMockServer(
		WireMockConfiguration.wireMockConfig()
			.port(portnummer)
			.extensions(*mocks.listOfExtension)
	)

	val navContext = mocks.navContext

	init {
		wireMockServer.stubFor(
			WireMock.post(mocks.skjermetPerson.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.skjermetPerson.name)
			)
		)
		wireMockServer.stubFor(
			WireMock.post(mocks.adgroupController.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.adgroupController.name)
			)
		)
		wireMockServer.stubFor(
			WireMock.post(mocks.tilgangsKontroller.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.tilgangsKontroller.name)
			)
		)
		wireMockServer.stubFor(
			WireMock.post(mocks.polecyController.path).willReturn(
				WireMock.aResponse().withTransformers(mocks.polecyController.name)
			)
		)

		wireMockServer.start()
	}
}
