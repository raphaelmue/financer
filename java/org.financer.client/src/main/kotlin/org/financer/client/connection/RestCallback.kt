package org.financer.client.connection

import org.financer.shared.connection.AsyncCall

interface RestCallback<T> : AsyncCall<T> {
    override fun onSuccess(result: T)
    override fun onFailure(exception: Exception) {}
    fun onBefore() {}
    override fun onAfter() {}
}