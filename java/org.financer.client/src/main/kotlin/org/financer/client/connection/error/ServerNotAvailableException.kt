package org.financer.client.connection.error

import org.financer.client.connection.RequestConfig
import org.financer.shared.exceptions.FinancerException

class ServerNotAvailableException(requestConfig: RequestConfig) : FinancerException(MESSAGE, MESSAGE_KEY, requestConfig.httpMethod.toString(), requestConfig.path) {
    companion object {
        private const val MESSAGE = "Request [%s: %s] failed. Server is not probably available."
        private const val MESSAGE_KEY = "exception.serverNotAvailable"
    }
}