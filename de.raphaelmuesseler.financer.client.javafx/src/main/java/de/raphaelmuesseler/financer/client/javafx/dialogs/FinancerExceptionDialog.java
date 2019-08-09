package de.raphaelmuesseler.financer.client.javafx.dialogs;

import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.Application;
import javafx.scene.layout.StackPane;

public class FinancerExceptionDialog extends FinancerAlert {
    public FinancerExceptionDialog(String header, Exception exception) {
        super(Application.MessageType.ERROR, header, new JavaFXFormatter(LocalStorageImpl.getInstance()).formatExceptionMessage(exception));
    }

    public FinancerExceptionDialog(StackPane container, String header, Exception exception) {
        super(container, Application.MessageType.ERROR, header, new JavaFXFormatter(LocalStorageImpl.getInstance()).formatExceptionMessage(exception));
    }
}
