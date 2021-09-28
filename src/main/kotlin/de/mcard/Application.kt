package de.mcard

import de.mcard.plugins.configureRouting
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        install(ContentNegotiation) {
            json()
        }
        install(Compression) {
            gzip()
        }
        configureRouting()
    }.start(wait = true)
}
