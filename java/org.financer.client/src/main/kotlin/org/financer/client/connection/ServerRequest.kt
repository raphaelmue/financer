package org.financer.client.connection

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import org.financer.client.connection.error.RestException
import org.financer.client.connection.error.ServerNotAvailableException
import org.financer.client.connection.error.UnauthorizedOperationException
import org.financer.client.domain.model.user.User
import org.financer.client.local.Application
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.value.objects.SettingPair
import java.io.IOException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ServerRequest(val requestConfig: RequestConfig) {
    companion object {
        private val DEFAULT_HOST = "https://api.financer-project.org/api/" +
                ServerRequest::class.java.getPackage().implementationVersion

        val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS) // For testing purposes
                .readTimeout(2, TimeUnit.SECONDS) // For testing purposes
                .writeTimeout(2, TimeUnit.SECONDS)
                .build()

        val objectMapper = ObjectMapper()

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

    private val logger = Logger.getLogger("org.financer.client")

    suspend inline fun <reified T : Any?> execute(): T? {
        try {
            val response = client.newCall(buildRequest()).await()
            val content = withContext(Dispatchers.IO) {
                response.body!!.string()
            }
            return handleResponse(response.code, content)
        } catch (e: UnknownHostException) {
            onFailure(e)
            throw ServerNotAvailableException(requestConfig)
        } catch (e: SocketException) {
            onFailure(e)
            throw ServerNotAvailableException(requestConfig)
        } catch (e: RestException) {
            onFailure(e)
            application.showToast(Application.MessageType.ERROR, e.restErrorMessage.messages)
        } catch (e: Exception) {
            onFailure(e)
        }
        return null
    }

    fun onFailure(e: Exception) {
        logger.log(Level.SEVERE, e.message, e)
    }

    @Throws(JsonProcessingException::class)
    suspend inline fun <reified T : Any?> handleResponse(code: Int, content: String): T {
        when (code) {
            200, 201 -> return withContext(Dispatchers.IO) { objectMapper.readValue(content, T::class.java) }
            403 -> throw UnauthorizedOperationException()
            else -> {
                val restErrorMessage = withContext(Dispatchers.IO) { objectMapper.readValue(content, RestErrorMessage::class.java) }
                throw RestException(restErrorMessage)
            }
        }
    }

    fun buildRequest(): Request {
        val user = localStorage!!.readObject<User>("user")
        val requestBuilder = Request.Builder()
                .url(buildUrl())
                .addHeader(HEADER_ACCEPT, MEDIA_TYPE_JSON.toString())
                .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON.toString())
        if (user?.activeToken?.token?.token != null) {
            requestBuilder.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + user.activeToken!!.token!!.token)
        }
        if (user != null) {
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