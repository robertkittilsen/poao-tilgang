# Felles tilgangskontroll for Produktområde Arbeidsoppfølging

API for tilgangskontroll med regler som er felles for Produktområde Arbeidsoppfølging.
Har også en HTTP-klient som kan trekkes inn som en avhengighet i applikasjoner for å kalle API'et.

## Bruk av HTTP-klient

Legg til jitpack som repository. Det er anbefalt å legge til Jitpack til slutt for å først søke igjennom
andre repositories for avhengigheter.

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
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```

Legg til avhengighet der `VERSION` = full SHA.

Maven:
```xml
<dependency>
    <groupId>com.github.navikt.poao-tilgang</groupId>
    <artifactId>client</artifactId>
    <version>VERSION</version>
</dependency>
```
```groovy
dependencies {
    implementation 'com.github.navikt.poao-tilgang:client:VERSION'
}
```
