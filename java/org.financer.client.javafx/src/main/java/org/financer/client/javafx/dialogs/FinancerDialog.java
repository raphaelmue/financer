package org.financer.client.javafx.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.financer.client.format.I18N;
import org.financer.client.javafx.main.FinancerController;
import org.financer.util.collections.Action;

/**
 * This class represents a Dialog, aligned with the needs of a general user interaction dialog. The design is base on
 * the JFXDialog {@link JFXDialog}.
 *
 * @param <T> class type that is to be the return type of this dialog
 */
public abstract class FinancerDialog<T> extends JFXDialog {
    private T value;
    private Label errorMessageLabel;
    private String errorMessage;

    private JFXDialogLayout dialogLayout;
    private Action<T> confirmAction;
    private Action<T> cancelAction;

    public FinancerDialog(T value) {
        this(value, (StackPane) ((BorderPane) ((FinancerController) FinancerController.getInstance()).getRootLayout().getCenter()).getCenter());
    }

    public FinancerDialog(T value, StackPane container) {
        super();
        this.value = value;

        // set container
        this.setDialogContainer(container);

        // set transition type
        this.setTransitionType(DialogTransition.CENTER);

        // set dialog content
        this.dialogLayout = new JFXDialogLayout();

        // initialize error message label
        this.errorMessageLabel = new Label();
        this.errorMessageLabel.setStyle("-fx-text-fill: #ff4a39;" +
                "    -fx-padding: 5 0 15 0;" +
                "    -fx-font-weight: 700;");
        this.errorMessageLabel.setManaged(false);

        Region dialogContent = this.getDialogContent();
        dialogLayout.setBody(new VBox(this.errorMessageLabel, dialogContent));

        // add action buttons
        JFXButton okBtn = new JFXButton(I18N.get("ok"));
        okBtn.setOnAction(event -> {
            if (checkConsistency()) {
                onConfirm();
                close();
            } else {
                showErrorMessage();
            }
        });

        JFXButton cancelBtn = new JFXButton(I18N.get("cancel"));
        cancelBtn.setOnAction(event -> {
            close();
            onCancel();
        });
        this.dialogLayout.setActions(okBtn, cancelBtn);

        this.setContent(this.dialogLayout);
        this.getContent().setPrefWidth(this.getDialogWidth());

        // show dialog
        this.show();
    }

    protected double getDialogWidth() {
        return 400;
    }

    private void showErrorMessage() {
        this.errorMessageLabel.setText(this.getErrorMessage());
        this.errorMessageLabel.setManaged(true);
    }

    protected final void setDialogTitle(String title) {
        this.dialogLayout.setHeading(new Text(title));
    }

    protected abstract boolean checkConsistency();

    protected abstract Region getDialogContent();

    protected void prepareDialogContent() {
    }

    protected final T getValue() {
        return value;
    }

    private String getErrorMessage() {
        return errorMessage;
    }

    protected void setValue(T value) {
        this.value = value;
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    protected void onCancel() {
        if (this.cancelAction != null) {
            this.cancelAction.action(getValue());
        }
    }

    protected void onConfirm() {
        if (this.confirmAction != null) {
            this.confirmAction.action(getValue());
        }
    }

    public void setOnConfirm(Action<T> confirmAction) {
        this.confirmAction = confirmAction;
    }

    public void setOnCancel(Action<T> cancelAction) {
        this.cancelAction = cancelAction;
    }
}
