package com.example.watching_android.model

import java.util.*

data class Event(
    var id: Int,
    var description: String,
    var createdAt: Date,
    var user: UserPublic
) {
    override fun toString(): String {
        return "$id - $description"
    }
}