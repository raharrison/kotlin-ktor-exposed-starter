package web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import model.NewWidget
import service.WidgetService

fun Route.widget(widgetService: WidgetService) {

    route("/widget") {

        get("/") {
            call.respond(widgetService.getAllWidgets())
        }

        get("/{id}") {
            val widget = widgetService.getWidget(call.parameters["id"]?.toInt()!!)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }

        post("/") {
            val widget = call.receive<NewWidget>()
            call.respond(HttpStatusCode.Created, widgetService.addWidget(widget))
        }

        put("/") {
            val widget = call.receive<NewWidget>()
            val updated = widgetService.updateWidget(widget)
            if(updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val removed = widgetService.deleteWidget(call.parameters["id"]?.toInt()!!)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }

    }

    webSocket("/updates") {
        try {
            widgetService.addChangeListener(this.hashCode()) {
                val out = jacksonObjectMapper().writeValueAsString(it)
                outgoing.send(Frame.Text(out))
            }
            while(true) incoming.receive()
        } finally {
            widgetService.removeChangeListener(this.hashCode())
        }
    }
}
