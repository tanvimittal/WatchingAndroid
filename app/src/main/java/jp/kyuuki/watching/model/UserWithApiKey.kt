package jp.kyuuki.watching.model

/**
 * This class stores the response of the api
 */
data class UserWithApiKey(
    var id: Int,
    var apiKey: String?
)