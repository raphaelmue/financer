module de.raphaelmuesseler.financer.client.javafx {
    requires transitive de.raphaelmuesseler.financer.client;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires com.jfoenix;
    requires org.controlsfx.controls;

    requires java.logging;
    requires java.desktop;

    opens de.raphaelmuesseler.financer.client.javafx.main to javafx.fxml, javafx.graphics;
    opens de.raphaelmuesseler.financer.client.javafx.login to javafx.fxml, javafx.graphics;
}