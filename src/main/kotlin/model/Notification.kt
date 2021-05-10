package model

import kotlinx.serialization.Serializable

enum class ChangeType { CREATE, UPDATE, DELETE}

@Serializable
data class Notification<T>(val type: ChangeType, val id: Int, val entity: T)

typealias WidgetNotification = Notification<Widget?>
