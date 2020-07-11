package org.financer.client.javafx.dialogs

import javafx.scene.layout.StackPane
import org.financer.client.local.Application

class FinancerExceptionDialog : FinancerAlert {
    constructor(header: String?, exception: Exception?) : super(Application.MessageType.ERROR, header, null) {}
    constructor(container: StackPane?, header: String?, exception: Exception?) : super(container, Application.MessageType.ERROR, header, null) {}
}