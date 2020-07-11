package org.financer.client.connection.error

import org.financer.client.connection.RestErrorMessage
import org.financer.shared.exceptions.FinancerException

class RestException(val restErrorMessage: RestErrorMessage) : FinancerException(restErrorMessage.messages!!.joinToString(separator = " "), "")