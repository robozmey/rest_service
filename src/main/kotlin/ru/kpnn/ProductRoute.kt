package ru.kpnn

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.head
import kotlinx.html.*

fun Route.productListRoute() {
    route("/product-list") {
        get {
            if (productStorage.isNotEmpty()) {
                call.respond(productStorage.values)
            } else {
                call.respondText("No product found", status = HttpStatusCode.OK)
            }
        }
    }
}

fun Route.productRoute() {
    route("/product") {
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val product =
                productStorage[id] ?: return@get call.respondText(
                    "No product with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title {
                        +"reult"
                    }
                }
                body {
                    h1 {
                        +product.name
                    }
                    h3 {
                        +product.description
                    }
                    img {
                        src = "/picture/${product.icon}"
                        height = "300"
                    }
                }
            }
        }
        post("{name?}{description?}") {
            val name = call.parameters["name"] ?: return@post call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )
            val description = call.parameters["description"] ?: return@post call.respondText(
                "Missing description",
                status = HttpStatusCode.BadRequest
            )
            val iconsFilenames = uploadFile(call)
            val product = Product(generateProductId(), name, description, iconsFilenames.firstOrNull())
            productStorage[product.productId] = product;
            call.respondText("Product stored correctly", status = HttpStatusCode.Created)
        }
        post("update/{id?}{name?}{description?}") {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val previousProduct = productStorage[id] ?: return@post call.respondText(
                "Product with id $id not found",
                status = HttpStatusCode.NotFound
            )
            val name = call.parameters["name"] ?:previousProduct.name
            val description = call.parameters["description"] ?: previousProduct.description
            val iconsFilenames = uploadFile(call)

            val product = Product(id, name, description, iconsFilenames.firstOrNull())
            productStorage[product.productId] = product;
            call.respondText("Product updated correctly", status = HttpStatusCode.Accepted)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (productStorage.remove(id) != null) {
                call.respondText("Product removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}