package org.financer.client.connection

import com.fasterxml.jackson.annotation.JsonProperty
import org.financer.shared.domain.model.api.DataTransferObject
import java.time.LocalDateTime

class RestErrorMessage : DataTransferObject {
    @JsonProperty("timestamp")
    var timestamp = LocalDateTime.now()
        private set

    @JsonProperty("path")
    var path: String? = null
        private set

    @JsonProperty("status")
    var statusCode = 0
        private set

    @JsonProperty("error")
    var error: String? = null
        private set

    @JsonProperty("messages")
    var messages: List<String>? = null
        private set

    fun setTimestamp(timestamp: LocalDateTime): RestErrorMessage {
        this.timestamp = timestamp
        return this
    }

    fun setPath(path: String?): RestErrorMessage {
        this.path = path
        return this
    }

    fun setStatusCode(statusCode: Int): RestErrorMessage {
        this.statusCode = statusCode
        return this
    }

    fun setError(error: String?): RestErrorMessage {
        this.error = error
        return this
    }

    fun setMessages(messages: List<String>?): RestErrorMessage {
        this.messages = messages
        return this
    }
}