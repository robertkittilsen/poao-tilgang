package no.nav.poao_tilgang.core.policy.test_utils

import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn
import no.nav.poao_tilgang.core.domain.AdGrupper
import java.util.*
import java.util.UUID.fromString

object TestAdGrupper {

	val randomGruppe = AdGruppe(UUID.randomUUID(), "some-other-group")

	val testAdGrupper = AdGrupper(
		fortroligAdresse = AdGruppe(fromString("97690ad9-d423-4c1f-9885-b01fb9f9feab"), AdGruppeNavn.FORTROLIG_ADRESSE),
		strengtFortroligAdresse = AdGruppe(fromString("49dfad60-e125-4216-b627-632f93054610"), AdGruppeNavn.STRENGT_FORTROLIG_ADRESSE),
		modiaAdmin = AdGruppe(fromString("d765c025-d56c-4b15-b824-a8e12d9de60e"), AdGruppeNavn.MODIA_ADMIN),
		modiaOppfolging = AdGruppe(fromString("d58e5b23-b7ea-4151-b6c1-8945c5438554"), AdGruppeNavn.MODIA_OPPFOLGING),
		modiaGenerell = AdGruppe(fromString("78d24b90-988a-4c6e-9862-3e0933ac2cd7"), AdGruppeNavn.MODIA_GENERELL),
		gosysNasjonal = AdGruppe(fromString("2866c090-cd46-4167-8e9e-4522d44312d0"), AdGruppeNavn.GOSYS_NASJONAL),
		gosysUtvidbarTilNasjonal = AdGruppe(fromString("b57870b5-3580-4e59-99f2-4c8c6083415d"), AdGruppeNavn.GOSYS_UTVIDBAR_TIL_NASJONAL),
		gosysUtvidet = AdGruppe(fromString("4ccf584e-098e-4625-aed7-97d82b450bcc"), AdGruppeNavn.GOSYS_UTVIDET),
		syfoSensitiv = AdGruppe(fromString("6681d1b1-e39f-4e34-b688-63584710772f"), AdGruppeNavn.SYFO_SENSITIV),
		pensjonUtvidet = AdGruppe(fromString("f7b20d6c-cf4b-47e0-b6ff-5383d9b6e57d"), AdGruppeNavn.PENSJON_UTVIDET)
	)


}
