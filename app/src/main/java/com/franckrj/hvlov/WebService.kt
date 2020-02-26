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

    private fun sendRequest(urlForPage: String, postContent: Map<String, String>?, tagToUse: Any?): String? {
        try {
            var currentUrlForPage = urlForPage

            while (true) {
                val request = Request.Builder().apply {
                    url(currentUrlForPage)

                    header("User-Agent", userAgentToUse)

                    if (postContent != null) {
                        val formBody = FormBody.Builder().apply {
                            for ((key, value) in postContent) {
                                add(key, value)
                            }
                        }.build()

                        post(formBody)
                    }

                    if (tagToUse != null) {
                        tag(tagToUse)
                    }
                }.build()

                _client.newCall(request).execute().use { response ->
                    if (response.isRedirect) {
                        currentUrlForPage = response.header("Location") ?: ""
                    } else {
                        return response.body?.string()
                    }
                }
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun getPage(urlForPage: String, tagToUse: Any? = null): String? {
        return sendRequest(urlForPage, null, tagToUse)
    }

    fun postPage(urlForPage: String, postContent: Map<String, String>, tagToUse: Any? = null): String? {
        return sendRequest(urlForPage, postContent, tagToUse)
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
