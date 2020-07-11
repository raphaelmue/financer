package org.financer.client.connection

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.financer.client.connection.ServerRequest.Companion.setLocalStorage
import org.financer.client.domain.model.user.User
import org.financer.client.local.Application
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.api.user.UserDTO
import org.financer.shared.domain.model.value.objects.Email
import org.financer.shared.domain.model.value.objects.Name
import org.financer.shared.path.PathBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

@Tag("skip")
@ExperimentalCoroutinesApi
class ServerRequestTest {

    private val server = MockWebServer()
    private var httpUrl: HttpUrl? = null

    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        val localStorage = Mockito.mock(LocalStorage::class.java)
        Mockito.`when`<Any?>(localStorage.readObject(ArgumentMatchers.any(String::class.java))).thenReturn(null)
        setLocalStorage(localStorage)
        ServerRequest.application = Mockito.mock(Application::class.java)
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (Objects.requireNonNull(request.path)) {
                    "/api/users" -> MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", ServerRequest.MEDIA_TYPE_JSON)
                            .setBody(UserDTO()
                                    .setEmail(Email("test@test.com"))
                                    .setName(Name("test", "Name"))
                                    .setId(1)
                                    .toJson())
                    "/api/categories" -> MockResponse()
                            .setResponseCode(403)
                            .setBody(RestErrorMessage()
                                    .setStatusCode(403)
                                    .setError("Unauthorized")
                                    .setTimestamp(LocalDateTime.now())
                                    .setMessages(listOf("The user is not allowed to perform this operation."))
                                    .toJson())
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Start the server.
        server.start()
        httpUrl = server.url("/api")
        Mockito.`when`<Any?>(localStorage.readObject("host")).thenReturn(httpUrl.toString())
        Mockito.`when`(localStorage.contains("host")).thenReturn(true)
    }


    @Test
    fun testCreateServerRequestSuccess() = runBlockingTest {
        val result = ServerRequest(RequestConfig(PathBuilder.Get().users().build())).execute<User?>()
        assert(result != null)
        assert(result!!.email!! == Email("test@test.com"))
    }

    @Test
    fun testCreateServerRequestUnauthorized() = runBlockingTest {
        val result = ServerRequest(RequestConfig(PathBuilder.Get().categories().build())).execute<User?>()
        assert(result != null)
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {
        server.shutdown()
    }
}