package no.nav.poao_tilgang.core.domain

data class AdGrupper(
	val fortroligAdresse: AdGruppe,
	val strengtFortroligAdresse: AdGruppe,

	val modiaAdmin: AdGruppe,
	val modiaOppfolging: AdGruppe,
	val modiaGenerell: AdGruppe,

	val gosysNasjonal: AdGruppe,
	val gosysUtvidbarTilNasjonal: AdGruppe,
	val gosysUtvidet: AdGruppe,

	val syfoSensitiv: AdGruppe,
	val pensjonUtvidet: AdGruppe,
	val egneAnsatte: AdGruppe,

	val aktivitetsplanKvp: AdGruppe
)
