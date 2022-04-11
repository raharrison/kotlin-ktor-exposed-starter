import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import service.DatabaseFactory
import service.WidgetService
import util.JsonMapper
import web.index
import web.widget

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(JsonMapper.defaultMapper)
    }

    install(ContentNegotiation) {
        json(JsonMapper.defaultMapper)
    }

    DatabaseFactory.connectAndMigrate()

    val widgetService = WidgetService()

    install(Routing) {
        index()
        widget(widgetService)
    }

}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}