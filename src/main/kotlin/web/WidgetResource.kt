package web

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.withContext
import model.NewWidget
import service.WidgetService
import util.JsonMapper.defaultMapper

@ExperimentalCoroutinesApi
fun Route.widget(widgetService: WidgetService) {

    route("/widget") {

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
                val output = withContext(Dispatchers.IO) {
                    defaultMapper.writeValueAsString(it)
                }
                outgoing.send(Frame.Text(output))
            }
            while (true) {
                incoming.receiveOrNull() ?: break
            }
        } finally {
            widgetService.removeChangeListener(this.hashCode())
        }
    }
}
