package com.franckrj.hvlov

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class WebService private constructor(private val userAgentToUse: String) {
    companion object {
        val instance: WebService by lazy { WebService("HVlov") }
    }

    private val _client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    fun getPage(urlForPage: String, tagToUse: Any = Object()): String? {
        return try {
            val request = Request.Builder()
                .url(urlForPage)
                .header("User-Agent", userAgentToUse)
                .tag(tagToUse)
                .build()

            _client.newCall(request).execute().body?.string()
        } catch (_: Exception) {
            null
        }
    }

    fun postPage(urlForPage: String, form: Map<String, String>, tagToUse: Any = Object()): String? {
        return try {
            val formBody = FormBody.Builder().apply {
                for ((key, value) in form) {
                    add(key, value)
                }
            }.build()

            val request = Request.Builder()
                .url(urlForPage)
                .header("User-Agent", userAgentToUse)
                .post(formBody)
                .tag(tagToUse)
                .build()

            _client.newCall(request).execute().body?.string()
        } catch (_: Exception) {
            null
        }
    }

    fun cancelRequest(withThisTag: Any) {
        for (call in _client.dispatcher.queuedCalls()) {
            if (call.request().tag() == withThisTag) {
                call.cancel()
            }
        }
        for (call in _client.dispatcher.runningCalls()) {
            if (call.request().tag() == withThisTag) {
                call.cancel()
            }
        }
    }
}
