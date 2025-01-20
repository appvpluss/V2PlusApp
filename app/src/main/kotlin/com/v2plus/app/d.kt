package com.v2plus.app

import com.v2plus.AESUtils

fun main() {
    // تولید کلید
    val secretKey = AESUtils.generateKey()

    // تبدیل کلید به رشته
    val keyString = AESUtils.keyToString(secretKey)
    println("کلید به صورت رشته: $keyString")

    // بازیابی کلید از رشته
    val restoredKey = AESUtils.stringToKey("bhQnX7ZD+wXKAZk3SKoI0w==")

    // متن اصلی
    val originalText = "http://soft-apis.site/v2plus.php"

    // رمزنگاری
    val encryptedText = AESUtils.encrypt(originalText, restoredKey)
    println("متن رمزنگاری شده: $encryptedText")

    // رمزگشایی
    val decryptedText = AESUtils.decrypt(encryptedText, restoredKey)
    println("متن رمزگشایی شده: $decryptedText")
}
