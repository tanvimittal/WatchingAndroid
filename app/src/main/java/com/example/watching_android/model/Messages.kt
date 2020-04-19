package com.example.watching_android.model

data class Messages (var id: Int,
                     var message: String){
    override fun toString(): String {
        return "$id - $message"
    }
}