module de.raphaelmuesseler.financer.client.javafx {
    requires de.raphaelmuesseler.financer.client;
    requires de.raphaelmuesseler.financer.shared;
    requires de.raphaelmuesseler.financer.util;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires com.jfoenix;
    requires org.controlsfx.controls;

    requires java.logging;
    requires java.desktop;

    exports de.raphaelmuesseler.financer.client.javafx.main to javafx.fxml, javafx.graphics;
    exports de.raphaelmuesseler.financer.client.javafx.login to javafx.fxml, javafx.graphics;
}