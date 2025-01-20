package com.v2plus

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64 // برای تبدیل Base64

object AESUtils {
    private const val ALGORITHM = "AES"

    // تولید کلید AES
    fun generateKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance(ALGORITHM)
        keyGen.init(128) // اندازه کلید
        return keyGen.generateKey()
    }

    // تبدیل SecretKey به رشته
    fun keyToString(secretKey: SecretKey): String {
        return Base64.encodeBase64String(secretKey.encoded)
    }

    // تبدیل رشته به SecretKey
    fun stringToKey(keyString: String): SecretKey {
        val decodedKey = Base64.decodeBase64(keyString)
        return SecretKeySpec(decodedKey, ALGORITHM)
    }

    // رمزنگاری متن
    fun encrypt(text: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.encodeBase64String(encryptedBytes)
    }

    // رمزگشایی متن
    fun decrypt(encryptedText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decodeBase64(encryptedText)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
