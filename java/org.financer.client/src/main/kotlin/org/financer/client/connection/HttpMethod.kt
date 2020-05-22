package org.financer.client.connection

import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.financer.shared.domain.model.api.DataTransferObject

interface HttpMethod {
    fun requestBody(body: DataTransferObject): RequestBody? {
        return body.toJson().toRequestBody(ServerRequest.MEDIA_TYPE_JSON)
    }

    fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder
    class Get : HttpMethod {
        override fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder {
            return builder.get()
        }

        override fun toString(): String {
            return "GET"
        }
    }

    class Post : HttpMethod {
        override fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder {
            return builder.post(requestBody(body!!)!!)
        }

        override fun toString(): String {
            return "POST"
        }
    }

    class Put : HttpMethod {
        override fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder {
            return builder.post(requestBody(body!!)!!)
        }

        override fun toString(): String {
            return "PUT"
        }
    }

    class Delete : HttpMethod {
        override fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder {
            return builder.delete()
        }

        override fun toString(): String {
            return "DELETE"
        }
    }

    class Patch : HttpMethod {
        override fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder {
            return builder.patch(requestBody(body!!)!!)
        }

        override fun toString(): String {
            return "PATCH"
        }
    }

    class Options : HttpMethod {
        override fun build(builder: Request.Builder, body: DataTransferObject?): Request.Builder {
            return builder.method("OPTIONS", null)
        }

        override fun toString(): String {
            return "OPTIONS"
        }
    }

    companion object {
        fun fromString(httpMethod: String?): HttpMethod {
            return when (httpMethod) {
                "GET" -> Get()
                "POST" -> Post()
                "PUT" -> Put()
                "DELETE" -> Delete()
                "PATCH" -> Patch()
                "OPTIONS" -> Options()
                else -> throw IllegalArgumentException("Illegal Http Method")
            }
        }
    }
}