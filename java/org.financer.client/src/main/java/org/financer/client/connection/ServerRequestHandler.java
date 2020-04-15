package org.financer.client.connection;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.financer.client.connection.error.RestException;
import org.financer.client.connection.error.ServerNotAvailableException;
import org.financer.client.connection.error.UnauthorizedOperationException;
import org.financer.client.domain.model.user.User;
import org.financer.client.local.Application;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRequestHandler<T> implements Runnable {

    private static final String DEFAULT_HOST = "https://api.financer-project.org/api/" +
            ServerRequestHandler.class.getPackage().getImplementationVersion();

    static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_LANGUAGE = "Accept-Language";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final Logger logger = Logger.getLogger("org.financer.client");

    private final RequestConfig requestConfig;
    private final RestCallback<T> restCallback;
    private final Class<T> type;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS) // For testing purposes
            .readTimeout(2, TimeUnit.SECONDS) // For testing purposes
            .writeTimeout(2, TimeUnit.SECONDS)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static LocalStorage localStorage;
    private static Application application;

    public ServerRequestHandler(RequestConfig requestConfig, Class<T> type, RestCallback<T> restCallback) {
        this.requestConfig = requestConfig;
        this.type = type;
        this.restCallback = restCallback;
    }

    public static void setLocalStorage(LocalStorage localStorage) {
        ServerRequestHandler.localStorage = localStorage;
    }

    public static void setApplication(Application application) {
        ServerRequestHandler.application = application;
    }

    @Override
    public void run() {
        try {
            Response response = client.newCall(buildRequest()).execute();
            final String content = Objects.requireNonNull(response.body()).string();
            handleResponse(response.code(), content);
        } catch (UnknownHostException | SocketException e) {
            onFailure(e);
            throw new ServerNotAvailableException(requestConfig);
        } catch (RestException e) {
            onFailure(e);
            application.showToast(Application.MessageType.ERROR, e.getRestErrorMessage().getMessages());
        } catch (Exception e) {
            onFailure(e);
        }
    }

    private void onFailure(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        restCallback.onFailure(e);
    }

    private void handleResponse(int code, String content) throws JsonProcessingException {
        switch (code) {
            case 200:
            case 201:
                restCallback.onSuccess(objectMapper.readValue(content, type));
                break;
            case 403:
                throw new UnauthorizedOperationException();
            default:
                RestErrorMessage restErrorMessage = objectMapper.readValue(content, RestErrorMessage.class);
                throw new RestException(restErrorMessage);
        }
    }

    private Request buildRequest() {
        if (MEDIA_TYPE_JSON == null) {
            throw new IllegalArgumentException("JSON media type is invalid");
        }

        final User user = localStorage.readObject("user");

        Request.Builder requestBuilder = new Request.Builder()
                .url(buildUrl())
                .addHeader(HEADER_ACCEPT, MEDIA_TYPE_JSON.toString())
                .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON.toString());

        if (user != null) {
            requestBuilder.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + user.getActiveToken().getToken().getToken());
            requestBuilder.addHeader(HEADER_LANGUAGE, user.getSettings().get(SettingPair.Property.LANGUAGE).getPair().getValue());
        }

        return requestConfig.getHttpMethod().build(requestBuilder, requestConfig.getBody()).build();
    }

    private HttpUrl buildUrl() {
        String host = DEFAULT_HOST;
        if (localStorage.contains("host")) {
            host = localStorage.readObject("host");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(host + requestConfig.getPath()).newBuilder();
        requestConfig.getQueryParameters().forEach(urlBuilder::addQueryParameter);

        return urlBuilder.build();
    }
}

