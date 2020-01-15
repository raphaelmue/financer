package org.financer.client.javafx.dialogs;

import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.local.Application;
import javafx.scene.layout.StackPane;

public class FinancerExceptionDialog extends FinancerAlert {
    public FinancerExceptionDialog(String header, Exception exception) {
        super(Application.MessageType.ERROR, header, new JavaFXFormatter(LocalStorageImpl.getInstance()).formatExceptionMessage(exception));
    }

    public FinancerExceptionDialog(StackPane container, String header, Exception exception) {
        super(container, Application.MessageType.ERROR, header, new JavaFXFormatter(LocalStorageImpl.getInstance()).formatExceptionMessage(exception));
    }
}
