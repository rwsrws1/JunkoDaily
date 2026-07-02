package com.example.learncompose.data.remote

import kotlinx.coroutines.delay
import java.security.SecureRandom
import java.util.Base64
import kotlin.time.Duration.Companion.milliseconds

object MockRemoteServer {

    fun generateBase64Token(length: Int = 32): String {
        val bytes = ByteArray(length)
        SecureRandom().nextBytes(bytes)
        // 使用 URL_SAFE 编码，避免生成 '/' 或 '+' 等可能在 URL 中引发问题的字符
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    suspend fun login(account: String, password: String): String {
        delay(500.milliseconds)
        return generateBase64Token()
    }
}