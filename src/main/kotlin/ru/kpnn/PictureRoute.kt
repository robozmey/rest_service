package ru.kpnn

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

const val FILEPATH = "/tmp"

suspend fun uploadFile(call: ApplicationCall) : List<Filename> {
    // retrieve all multipart data (suspending)
    println("||||" + call.request.contentType().toString() + "||||")
    if("multipart/form-data" !in call.request.contentType().toString())
        return listOf()

    val multipart = call.receiveMultipart()

    val filenames = mutableListOf<Filename>()

    multipart.forEachPart { part ->
        // if part is a file (could be form item)
        if(part is PartData.FileItem) {
            // retrieve file name of upload
            val name = checkFilename(part.originalFileName!!)

            pictureStorage.add(name)
            filenames.add(name)

            val file = File("$FILEPATH/$name")

            // use InputStream from part to save file
            part.streamProvider().use { its ->
                // copy the stream to the file with buffering
                file.outputStream().buffered().use {
                    // note that this is blocking
                    its.copyTo(it)
                }
            }
        }
        // make sure to dispose of the part after use to prevent leaks
        part.dispose()
    }

    return filenames
}

fun Route.pictureRoute() {
    route("/picture") {
        get("/{name}") {
            // get filename from request url
            val filename = call.parameters["name"]!!

            if (!pictureStorage.contains(filename))
                return@get call.respond(HttpStatusCode.NotFound)
            // construct reference to file
            // ideally this would use a different filename
            val file = File("$FILEPATH/$filename")
            if(file.exists()) {
                call.respondFile(file)
            }
            else call.respond(HttpStatusCode.NotFound)
        }
        post("/upload") { _ ->
            uploadFile(call)

            call.respond(HttpStatusCode.Created)
        }
    }
}