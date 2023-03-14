package no.nav.poao_tilgang.application

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PreDestroy
import kotlin.concurrent.thread

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
