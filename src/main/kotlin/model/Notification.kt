package model

enum class ChangeType { CREATE, UPDATE, DELETE}

data class Notification<T:Any?>(val type: ChangeType, val id: Int, val entity: T)