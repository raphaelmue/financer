package org.financer.client.connection

import org.financer.shared.domain.model.api.DataTransferObject
import org.financer.shared.path.Path

class RequestConfig(val httpMethod: HttpMethod, val path: String, val queryParameters: Map<String?, String?>, val body: DataTransferObject?) {

    constructor(path: Path, body: DataTransferObject?) : this(HttpMethod.Companion.fromString(path.method), path.path, emptyMap<String?, String>(), body) {}

    @JvmOverloads
    constructor(path: Path, parameters: Map<String?, String?> = emptyMap<String?, String>()) : this(HttpMethod.Companion.fromString(path.method), path.path, parameters, object : DataTransferObject {
        override fun toJson(): String {
            return ""
        }
    }) {
    }

}