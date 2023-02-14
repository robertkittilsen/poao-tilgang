package no.nav.poao_tilgang.core.policy.test_utils

import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn
import no.nav.poao_tilgang.core.domain.AdGrupper
import java.util.*

object TestAdGrupper {

	val randomGruppe = AdGruppe(UUID.randomUUID(), "some-other-group")

	val testAdGrupper = AdGrupper(
		fortroligAdresse = AdGruppe(UUID.randomUUID(), AdGruppeNavn.FORTROLIG_ADRESSE),
		strengtFortroligAdresse = AdGruppe(UUID.randomUUID(), AdGruppeNavn.STRENGT_FORTROLIG_ADRESSE),
		modiaAdmin = AdGruppe(UUID.randomUUID(), AdGruppeNavn.MODIA_ADMIN),
		modiaOppfolging = AdGruppe(UUID.randomUUID(), AdGruppeNavn.MODIA_OPPFOLGING),
		modiaGenerell = AdGruppe(UUID.randomUUID(), AdGruppeNavn.MODIA_GENERELL),
		gosysNasjonal = AdGruppe(UUID.randomUUID(), AdGruppeNavn.GOSYS_NASJONAL),
		gosysUtvidbarTilNasjonal = AdGruppe(UUID.randomUUID(), AdGruppeNavn.GOSYS_UTVIDBAR_TIL_NASJONAL),
		gosysUtvidet = AdGruppe(UUID.randomUUID(), AdGruppeNavn.GOSYS_UTVIDET),
		syfoSensitiv = AdGruppe(UUID.randomUUID(), AdGruppeNavn.SYFO_SENSITIV),
		pensjonUtvidet = AdGruppe(UUID.randomUUID(), AdGruppeNavn.PENSJON_UTVIDET),
		egneAnsatte = AdGruppe(UUID.randomUUID(), AdGruppeNavn.EGNE_ANSATTE),
		aktivitetsplanKvp = AdGruppe(UUID.randomUUID(), AdGruppeNavn.AKTIVITETSPLAN_KVP)
	)


}
