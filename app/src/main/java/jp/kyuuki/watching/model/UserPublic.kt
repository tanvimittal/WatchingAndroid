package jp.kyuuki.watching.model

data class UserPublic(
    var id: Int,
    var nickname: String
) {
    companion object {
        const val MAX_LENGTH_NICKNAME = 15
    }
}