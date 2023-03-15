package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.http.HttpResponse
import java.util.UUID

class NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImplTest {

	private val norskIdent = "63546454"
	private val navAnsattAzureId = UUID.randomUUID()
	private val navEnhet = "1234"

	private val oppfolgingsenhetProvider = mockk<OppfolgingsenhetProvider>()
	private val geografiskTilknyttetEnhetProvider = mockk<GeografiskTilknyttetEnhetProvider>()
	private val adGruppeProvider = mockk<AdGruppeProvider>()
	private val navEnhetTilgangProvider = mockk<NavEnhetTilgangProvider>()

	private lateinit var policy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns testAdGrupper

		policy = NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
			oppfolgingsenhetProvider,
			geografiskTilknyttetEnhetProvider,
			adGruppeProvider,
			navEnhetTilgangProvider
		)
	}

	@Test
	internal fun `skal ikke sjekke tilgang til enhet og returnere permit hvis tilgang til "0000-GA-GOSYS_NASJONAL"`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(testAdGrupper.gosysNasjonal)

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit

		verify(exactly = 0) {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(any())
		}

		verify(exactly = 0) {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(any())
		}
	}

	@Test
	internal fun `skal ikke sjekke tilgang til enhet og returnere permit hvis tilgang til "0000-GA-GOSYS_UTVIDBAR_TIL_NASJONAL"`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(testAdGrupper.gosysUtvidbarTilNasjonal)

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit

		verify(exactly = 0) {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(any())
		}

		verify(exactly = 0) {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(any())
		}
	}

	@Test
	internal fun `skal sjekke tilgang til oppfølgingsenhet hvis finnes geografisk enhet for bruker ikke finnes`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns ""

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} returns null

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns navEnhet

		every { navEnhetTilgangProvider.hentEnhetTilganger(any()) } returns listOf(
			NavEnhetTilgang(
				navEnhet,
				"",
				listOf()
			)
		)

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit

		verify(exactly = 1) {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(any())
		}
	}

	@Test
	internal fun `skal sjekke tilgang til oppfølgingsenhet hvis finnes geografisk ikke finnes over tilganger til veileder`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns ""

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} returns "12345"

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns navEnhet

		every { navEnhetTilgangProvider.hentEnhetTilganger(any()) } returns listOf(
			NavEnhetTilgang(
				navEnhet,
				"",
				listOf()
			)
		)

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit

		verify(exactly = 1) {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(any())
		}
	}

	@Test
	internal fun `skal sjekke tilgang til geografisk enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns ""

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns null

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} returns navEnhet

		every { navEnhetTilgangProvider.hentEnhetTilganger(any()) } returns listOf(
			NavEnhetTilgang(
				navEnhet,
				"",
				listOf()
			)
		)

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Permit
		verify(exactly = 0) {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(any())
		}
	}

	@Test
	internal fun `skal returnere DENY om oppfølgingsenhet og geografisk enhet er null`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns null

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} returns null

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)

		decision shouldBe Decision.Deny(
			message = "Brukeren har ikke oppfølgingsenhet eller geografisk enhet",
			reason = DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
		)
	}

	@Test
	internal fun `skal gå videre til sjekk av oppfølgingsenhet dersom man får 404 fra norg (geografisk tilknytning)`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)
		} returns null

		every {
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknytetEnhet(norskIdent)
		} throws java.lang.UnsupportedOperationException()

		val decision = policy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		)
		verify(exactly = 1) {
			oppfolgingsenhetProvider.hentOppfolgingsenhet(any())
		}
	}


}
