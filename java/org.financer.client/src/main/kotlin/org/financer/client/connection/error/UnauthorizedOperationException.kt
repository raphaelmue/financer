package org.financer.client.connection.error

import org.financer.shared.exceptions.FinancerException

class UnauthorizedOperationException : FinancerException(
        "User is not allowed to perform this action",
        "exception.unauthorizedOperation")