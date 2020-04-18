package org.financer.client.connection;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.financer.client.domain.model.user.User;
import org.financer.client.local.Application;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.financer.shared.domain.model.value.objects.Email;
import org.financer.shared.domain.model.value.objects.Name;
import org.financer.shared.path.PathBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
public class ServerRequestTest {

    private final MockWebServer server = new MockWebServer();
    private HttpUrl httpUrl;

    @BeforeEach
    public void setUp() throws IOException {
        if (ServerRequest.MEDIA_TYPE_JSON == null) {
            throw new IllegalArgumentException("Illegal media type");
        }

        final LocalStorage localStorage = Mockito.mock(LocalStorage.class);
        when(localStorage.readObject(any(String.class))).thenReturn(null);

        ServerRequest.setLocalStorage(localStorage);
        ServerRequest.setApplication(Mockito.mock(Application.class));

        server.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                switch (Objects.requireNonNull(request.getPath())) {
                    case "/api/users":
                        return new MockResponse()
                                .setResponseCode(200)
                                .setHeader("Content-Type", ServerRequest.MEDIA_TYPE_JSON)
                                .setBody(new UserDTO()
                                        .setEmail(new Email("test@test.com"))
                                        .setName(new Name("test", "Name"))
                                        .setId(1)
                                        .toJson());
                    case "/api/categories":
                        return new MockResponse()
                                .setResponseCode(403)
                                .setBody(new RestErrorMessage()
                                        .setStatusCode(403)
                                        .setError("Unauthorized")
                                        .setTimestamp(LocalDateTime.now())
                                        .setMessages(Collections.singletonList("The user is not allowed to perform this operation."))
                                        .toJson());

                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        });

        // Start the server.
        server.start();
        httpUrl = server.url("/api");

        when(localStorage.readObject("host")).thenReturn(httpUrl.toString());
        when(localStorage.contains("host")).thenReturn(true);
    }

    @Test
    public void testCreateServerRequestSuccess() {
        ServerRequest<User> serverRequest = new ServerRequest<>(new RequestConfig(
               PathBuilder.Get().users().build()), User.class, result -> {
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(new Email("test@test.com"));
        });
        serverRequest.run();
    }

    @Test
    public void testCreateServerRequestUnauthorized() {
        ServerRequest<User> serverRequest = new ServerRequest<>(new RequestConfig(
                PathBuilder.Get().categories().build()), User.class, new RestCallback<>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onFailure(Exception exception) {
                assertThat(exception).isNotNull();
            }
        });
        serverRequest.run();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.shutdown();
    }

}