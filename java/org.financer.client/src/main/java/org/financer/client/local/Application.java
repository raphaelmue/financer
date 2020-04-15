package org.financer.client.local;

import java.util.List;

public interface Application {
    public enum MessageType {
        ERROR("error"),
        INFO("info"),
        WARNING("warning"),
        SUCCESS("success");

        private final String name;

        MessageType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    void showLoadingBox();

    void hideLoadingBox();

    void setOffline();

    void setOnline();

    void showToast(MessageType messageType, String message);

    default void showToast(MessageType messageType, List<String> messages) {
        messages.forEach(message -> showToast(messageType, message));
    }

    void showErrorDialog(Exception exception);
}
