package web

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.index() {

    val indexPage = javaClass.getResource("/pages/index.html")?.readText() ?: error("index.html resource not found")

    get("/") {
        call.respondText(indexPage, ContentType.Text.Html)
    }
}
