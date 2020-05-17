package org.financer.client.javafx.dialogs;

import javafx.scene.layout.StackPane;
import org.financer.client.local.Application;

public class FinancerExceptionDialog extends FinancerAlert {
    public FinancerExceptionDialog(String header, Exception exception) {
        super(Application.MessageType.ERROR, header, null);
    }

    public FinancerExceptionDialog(StackPane container, String header, Exception exception) {
        super(container, Application.MessageType.ERROR, header, null);
    }
}
