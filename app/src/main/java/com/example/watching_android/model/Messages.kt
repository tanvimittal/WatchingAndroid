package com.example.watching_android.model

import android.app.Notification
import java.util.*

data class Messages (var id: Int,
                     var description: String,
                     var created_at : Date,
                     var user: NickNameID
                     ){
    override fun toString(): String {
        return "$id - $description"
    }
}