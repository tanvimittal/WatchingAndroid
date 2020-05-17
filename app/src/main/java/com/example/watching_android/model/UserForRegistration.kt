package com.example.watching_android.model

/**
 * DataClass for storing UserName and Phone Number
 */
data class UserForRegistration(
    val phoneNumber: PhoneNumber
)

// 通常、この形の電話番号はデータとしてもたない。ユーザー登録のための特別な形
data class PhoneNumber (
    val countryCode: String,
    val original: String
)