package ru.kpnn.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import ru.kpnn.pictureRoute
import ru.kpnn.productListRoute
import ru.kpnn.productRoute

fun Application.configureRouting() {
    routing {
        productRoute()
        productListRoute()

        pictureRoute()
    }
}
