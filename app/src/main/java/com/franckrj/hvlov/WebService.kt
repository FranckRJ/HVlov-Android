package com.franckrj.hvlov

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

// TODO: The functions are not well designed here. Should be redone with explicits parameter, a way to get the error, etc.

/**
 * Service for sending web requests (HTTP).
 *
 * @property userAgentToUse The user agent used for the requests.
 */
class WebService private constructor(private val userAgentToUse: String) {
    companion object {
        val instance: WebService by lazy { WebService("HVlov") }
    }

    /**
     * The [OkHttpClient] used for sending the HTTP requests.
     */
    private val _client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    /**
     * Function for sending an HTTP request. Send either POST request if [postContent] is not null or GET if it is.
     *
     * @param urlForPage The URL for the HTTP request.
     * @param postContent The list of key/value pair to send in the request. If not null the request will use the POST
     *                    method. Otherwise it will be a GET.
     * @param tagToUse A tag to identify the request, to be able to cancel it later. If null the request won't be tagged.
     * @return The response of the request, or null if an error happened.
     */
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

    /**
     * Send a GET request to the page.
     *
     * @param urlForPage The URL for the request.
     * @param tagToUse A tag to identify the request, to be able to cancel it later. If null the request won't be tagged.
     * @return The response of the request, or null if an error happened.
     */
    fun getPage(urlForPage: String, tagToUse: Any? = null): String? {
        return sendRequest(urlForPage, null, tagToUse)
    }

    /**
     * Send a POST request with key/value data.
     *
     * @param urlForPage The URL for the request.
     * @param postContent The list of key/value pair to send in the request.
     * @param tagToUse A tag to identify the request, to be able to cancel it later. If null the request won't be tagged.
     * @return The response of the request, or null if an error happened.
     */
    fun postPage(urlForPage: String, postContent: Map<String, String>, tagToUse: Any? = null): String? {
        return sendRequest(urlForPage, postContent, tagToUse)
    }

    /**
     * Cancel a running or pending request.
     *
     * @param withThisTag Tag used to identify the request to cancel.
     */
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
