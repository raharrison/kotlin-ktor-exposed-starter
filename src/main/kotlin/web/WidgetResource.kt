package web

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import model.NewWidget
import service.WidgetService

fun Route.widget(widgetService: WidgetService) {

    route("/widgets") {

        get {
            call.respond(widgetService.getAllWidgets())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val widget = widgetService.getWidget(id)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }

        post {
            val widget = call.receive<NewWidget>()
            call.respond(HttpStatusCode.Created, widgetService.addWidget(widget))
        }

        put {
            val widget = call.receive<NewWidget>()
            val updated = widgetService.updateWidget(widget)
            if (updated == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
            val removed = widgetService.deleteWidget(id)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }

    }

    webSocket("/updates") {
        try {
            widgetService.addChangeListener(this.hashCode()) {
                sendSerialized(it)
            }
            for (frame in incoming) {
                if (frame.frameType == FrameType.CLOSE) {
                    break
                } else if (frame is Frame.Text) {
                    call.application.environment.log.info("Received websocket message: {}", frame.readText())
                }
            }
        } finally {
            widgetService.removeChangeListener(this.hashCode())
        }
    }
}
