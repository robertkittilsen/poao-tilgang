# Felles tilgangskontroll for Produktområde Arbeidsoppfølging

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=bugs)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=code_smells)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=security_rating)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)

API for tilgangskontroll med regler som er felles for Produktområde Arbeidsoppfølging.
Har også en HTTP-klient som kan trekkes inn som en avhengighet i applikasjoner for å kalle API'et.

## Bruk av HTTP-klient

### 1.Legg til jitpack som repository
Det er anbefalt å legge til Jitpack til slutt for å først søke igjennom andre repositories for avhengigheter.

Maven:
```xml
    <repositories>
        <!-- Legger til central eksplisitt for prioritet over jitpack -->
        <repository>
            <id>central</id>
            <url>https://repo.maven.apache.org/maven2</url>
        </repository>
        <repository>
            <id>jitpack</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```

Gradle:
```groovy
repositories {
    // Legger til central eksplisitt for prioritet over jitpack
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```

### 2. Legg til dependency for klienten 

Hent siste versjon av klienten på https://github.com/navikt/poao-tilgang/releases og legg til avhengigheten.
NB: Jitpack bygger avhengigheter lazy, dvs når noen først prøver å laste avhengigheten ned. Hvis du er den første som laster 
ned en ny versjon, så kan det potensielt ta noen minutter før avhengigheten er bygget ferdig. 

Maven:
```xml
<dependency>
    <groupId>com.github.navikt.poao-tilgang</groupId>
    <artifactId>client</artifactId>
    <version>YYYY.MM.DD_HH.mm-SHA</version>
</dependency>
```

Gradle:
```groovy
dependencies {
    implementation 'com.github.navikt.poao-tilgang:client:YYYY.MM.DD_HH.mm-SHA'
}
```

### 3. Legg til applikasjonen din i poao-tilgang sin ACL
```yaml
# nais-(dev|prod).yaml
 accessPolicy:
   inbound:
     rules:
       - application: <application>
         namespace: <namespace>
         cluster: <cluster>
```

### 4. Opprett en instans av klienten

```kotlin
 val client: PoaoTilgangClient = PoaoTilgangCachedClient(
    PoaoTilgangHttpClient(
        baseUrl = "http://poao-tilgang.poao.svc.cluster.local", // or use "https://poao-tilgang(.dev).intern.nav.no" if your sending the request from dev-fss/prod-fss
        tokenProvider = { "machine-to-machine token" }
    )
)
```

### 5. Ta klienten i bruk

```kotlin
val decision = client.evaluatePolicy(NavAnsattTilgangTilEksternBrukerPolicyInput(
    navAnsattAzureId = UUID.fromString("some id"), // Dette kan hentes fra "oid"-claimet til en NAV ansatt sitt Azure AD JWT token 
    tilgangType = TilgangType.LESE,
    norskIdent = "01234567890" // fnr, dnr etc
)).getOrThrow()

println("Nav ansatt har lesetilgang til bruker: ${decision.isPermit}")
```
## Klient API

```kotlin
interface PoaoTilgangClient {

	/**
	 * Evaluer en policy med gitt input, sjekk {@link no.nav.poao_tilgang.client.PolicyInput.kt} for hvilke policies som er tilgjengelig
	 */
	fun evaluatePolicy(input: PolicyInput): ApiResult<Decision>

	/**
	 * Evaluer flere policies, sjekk {@link no.nav.poao_tilgang.client.PolicyInput.kt} for hvilke policies som er tilgjengelig
	 */
	fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>>

	/**
	 * Henter alle Azure AD-grupper til en NAV Ansatt ved bruk av objekt IDen til den ansatte
	 */
	fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>>

	/**
	 * Henter om en enkelt person er skjermet. Skjermet person var tidligere kjent som "egen ansatt"
	 */
	fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean>

	/**
	 * Henter om flere personer er skjermet. Skjermet person var tidligere kjent som "egen ansatt"
	 */
	fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>>

}
```

## Tilgjengelig policies

```kotlin
/*
 Sjekker om en NAV ansatt har lese- eller skrivetilgang til en ekstern bruker.
 For funksjoner som gjør endringer på data så ønsker man oftest å benytte sjekk på skrivetilgang,
 mens for funksjoner som kun henter data så benyttes oftest lesetilgang.
 
 Veiledere har både lese- og skrivetilgang, mens f.eks NKS har kun lesetilgang.
*/
data class NavAnsattTilgangTilEksternBrukerPolicyInput(
	val navAnsattAzureId: UUID,
	val tilgangType: TilgangType,
	val norskIdent: String
) : PolicyInput()

/* 
 Sjekker om en NAV ansatt har tilgang til å bruke Modia-flaten
*/
data class NavAnsattTilgangTilModiaPolicyInput(
	val navAnsattAzureId: UUID
) : PolicyInput()

/* 
 Sjekker om en NAV ansatt har tilgang til spesifik NAVenhet
*/
data class NavAnsattTilgangTilNavEnhetPolicyInput(
    val navAnsattAzureId: UUID,
    val navEnhetId: String
) : PolicyInput()

/* 
 Sjekker om en NAV ansatt kan behandle **strengt** fortrolig brukere (kode 6)
*/
data class NavAnsattBehandleStrengtFortroligBrukerePolicyInput(
    val navAnsattAzureId: UUID
) : PolicyInput()

/* 
 Sjekker om en NAV ansatt kan behandle fortrolig brukere (kode 7)
*/
data class NavAnsattBehandleFortroligBrukerePolicyInput(
    val navAnsattAzureId: UUID
) : PolicyInput()

/* 
 Sjekker om en NAV ansatt kan behandle skjermede personer (egne ansatte)
*/
data class NavAnsattBehandleSkjermedePersonerPolicyInput(
    val navAnsattAzureId: UUID
) : PolicyInput()


/* 
 Sjekker om en NAV ansatt har tilgang til NAV enhet med sperre. Brukes i forbindelse med KVP.
*/
data class NavAnsattTilgangTilNavEnhetMedSperrePolicyInput(
    val navAnsattAzureId: UUID,
    val navEnhetId: String
) : PolicyInput()
```

## Prosjektstruktur

Poao-tilgang er delt opp i flere moduler for å gjøre det enklere å vedlikeholde en tydelig arkitektur.

Modulene er som følger:
* **api** - felles DTOer som brukes av _client-modulen_ og _application-modulen_
* **application** - kjører opp applikasjonen, definerer endepunkter, etc. Tar i bruk _core-modulen_ for å eksponere tilgangskontrollregler med et REST API
* **client** - brukes ikke direkte av poao-tilgang, men av andre konsumerende applikasjoner som ønsker en ferdig testet klient for å gjøre requests mot poao-tilgang
* **core** - inneholder implementasjon og definisjon av alle de ulike tilgangskontrollreglene til poao-tilgang   