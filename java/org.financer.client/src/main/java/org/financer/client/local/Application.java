package org.financer.client.local;

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

    void showErrorDialog(Exception exception);
}
