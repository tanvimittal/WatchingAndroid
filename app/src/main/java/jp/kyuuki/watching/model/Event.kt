package jp.kyuuki.watching.model

import java.util.*

data class Event(
    var id: Int,
    var name: String,
    var createdAt: Date,
    var user: UserPublic
) {
    companion object {
        // 起床イベント
        const val NAME_GET_UP = "get_up"

        // 就寝イベント
        const val NAME_GO_TO_BED = "go_to_bed"
    }

    override fun toString(): String {
        return "$id - $name"
    }
}