package com.v2plus.app.util

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64
import java.nio.charset.Charset

object AA {

    fun AAAA(A: String): SecretKey {
        return SecretKeySpec(Base64.decodeBase64(A), String(Base64.decodeBase64("QUVT"), Charset.forName(String(Base64.decodeBase64("VVRGLTg=")))))
    }


    fun AAAAAAA(A: String, AA: SecretKey): String {
        val cipher = Cipher.getInstance(String(Base64.decodeBase64("QUVT"), Charset.forName(String(Base64.decodeBase64("VVRGLTg=")))))
        cipher.init(Cipher.DECRYPT_MODE, AA)
        return String(cipher.doFinal(Base64.decodeBase64(A)), Charset.forName(String(Base64.decodeBase64("VVRGLTg="))))
    }
}
