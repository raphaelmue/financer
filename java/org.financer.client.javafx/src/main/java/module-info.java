module org.financer.client.javafx {
    requires transitive org.financer.client;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires com.jfoenix;
    requires org.controlsfx.controls;

    requires java.logging;
    requires java.desktop;

    opens org.financer.client.javafx.main to javafx.fxml, javafx.graphics;
    opens org.financer.client.javafx.login to javafx.fxml, javafx.graphics;
    opens org.financer.client.javafx.components.charts to javafx.fxml;
    opens org.financer.client.javafx.main.overview to javafx.fxml;
    opens org.financer.client.javafx.main.profile to javafx.fxml;
    opens org.financer.client.javafx.main.settings to javafx.fxml;
    opens org.financer.client.javafx.main.statistics to javafx.fxml;
    opens org.financer.client.javafx.main.transactions to javafx.fxml;

    exports org.financer.client.javafx.components to javafx.fxml;

}