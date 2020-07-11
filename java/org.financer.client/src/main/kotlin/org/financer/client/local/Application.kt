package org.financer.client.local

import java.util.function.Consumer

interface Application {
    enum class MessageType(val type: String) {
        ERROR("error"),
        INFO("info"),
        WARNING("warning"),
        SUCCESS("success");
    }

    fun showLoadingBox()
    fun hideLoadingBox()
    fun setOffline()
    fun setOnline()
    fun showToast(messageType: MessageType?, message: String?)
    fun showToast(messageType: MessageType?, messages: List<String?>?) {
        messages!!.forEach(Consumer { message: String? -> showToast(messageType, message) })
    }

    fun showErrorDialog(exception: Exception?)
}