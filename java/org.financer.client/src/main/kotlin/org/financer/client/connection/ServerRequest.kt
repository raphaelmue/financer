package org.financer.client.connection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import org.financer.client.connection.error.RestException
import org.financer.client.connection.error.UnauthorizedOperationException
import org.financer.client.domain.model.user.User
import org.financer.client.local.Application
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.value.objects.SettingPair
import java.io.IOException
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class ServerRequest(private val requestConfig: RequestConfig) {
    companion object {
        private const val DEFAULT_HOST = "https://api.financer-project.org/api/1.0-SNAPSHOT"
//        private const val DEFAULT_HOST = "http://localhost:3000/api/1.0-SNAPSHOT"

        val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS) // For testing purposes
                .readTimeout(2, TimeUnit.SECONDS) // For testing purposes
                .writeTimeout(2, TimeUnit.SECONDS)
                .build()

        val gson: Gson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
                    LocalDate.parse(json.asJsonPrimitive.asString)
                }).create()

        @JvmStatic
        lateinit var application: Application
        private var localStorage: LocalStorage? = null

        @kotlin.jvm.JvmField
        val MEDIA_TYPE_JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        private const val HEADER_ACCEPT = "Accept"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_LANGUAGE = "Accept-Language"
        private const val TOKEN_PREFIX = "Bearer "


        @kotlin.jvm.JvmStatic
        fun setLocalStorage(localStorage: LocalStorage?) {
            Companion.localStorage = localStorage
        }
    }

    suspend inline fun <reified T : Any?> execute(): T? {
        val response = client.newCall(buildRequest()).await()
        val content = withContext(Dispatchers.IO) {
            response.body!!.string()
        }
        when (response.code) {
            200, 201 -> return withContext(Dispatchers.IO) {
                gson.fromJson(content, T::class.java)
            }
            403 -> throw UnauthorizedOperationException()
            else -> {
                val restErrorMessage = withContext(Dispatchers.IO) { gson.fromJson(content, RestErrorMessage::class.java) }
                throw RestException(restErrorMessage)
            }
        }
    }

    fun buildRequest(): Request {
        val requestBuilder = Request.Builder()
                .url(buildUrl())
                .addHeader(HEADER_ACCEPT, MEDIA_TYPE_JSON.toString())
                .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON.toString())
        val user = localStorage!!.readObject<User>("user")
        if (user != null) {
            if (user.activeToken?.token?.token != null) {
                requestBuilder.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + user.activeToken!!.token!!.token)
            }
            requestBuilder.addHeader(HEADER_LANGUAGE, user.getValueOrDefault<Locale>(SettingPair.Property.LANGUAGE).language)
        }
        return requestConfig.httpMethod.build(requestBuilder, requestConfig.body).build()
    }

    private fun buildUrl(): HttpUrl {
        val urlBuilder = buildUrlWithPathParameters().toHttpUrlOrNull()!!.newBuilder()
        requestConfig.queryParameters.forEach { (name: String?, value: String?) -> urlBuilder.addQueryParameter(name!!, value) }
        return urlBuilder.build()
    }

    private fun buildUrlWithPathParameters(): String {
        var host: String? = DEFAULT_HOST
        if (localStorage!!.contains("host")) {
            host = localStorage!!.readObject("host")
        }
        var url = host + requestConfig.path

        // replace path parameters with actual value and remove parameter from requestConfig.getQueryParameters()
        for ((key, value) in requestConfig.queryParameters) {
            url = url.replace("{$key}", value!!)
        }
        return url
    }

}

/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return Result of request or throw exception
 */
suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                // Don't bother with resuming the continuation if it is already cancelled.
                if (continuation.isCancelled) return
                continuation.resumeWithException(e)
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                println(ex.stackTrace)
            }
        }
    }
}