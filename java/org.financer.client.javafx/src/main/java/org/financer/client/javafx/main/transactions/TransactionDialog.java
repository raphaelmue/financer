package org.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DatePicker;
import org.financer.client.javafx.components.DoubleField;
import org.financer.client.javafx.dialogs.FileChooserDialog;
import org.financer.client.javafx.dialogs.FinancerConfirmDialog;
import org.financer.client.javafx.dialogs.FinancerDialog;
import org.financer.client.javafx.dialogs.FinancerExceptionDialog;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.model.transactions.ContentAttachment;
import org.financer.util.collections.TreeUtil;
import org.financer.util.concurrency.FinancerExecutor;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class TransactionDialog extends FinancerDialog<VariableTransaction> {

    private DoubleField amountField;
    private JFXComboBox<CategoryTree> categoryComboBox;
    private JFXTextField productField;
    private JFXTextField purposeField;
    private JFXTextField shopField;
    private DatePicker valueDateField;
    private JFXListView<Attachment> attachmentListView;
    private BaseCategory categories;

    private final Logger logger = Logger.getLogger("FinancerApplication");

    TransactionDialog(VariableTransaction transaction, BaseCategory categories) {
        super(transaction);

        this.categories = categories;
        this.prepareDialogContent();
        this.setDialogTitle(I18N.get("transaction"));
    }

    @Override
    protected Region getDialogContent() {
        HBox hBox = new HBox();
        hBox.setSpacing(30);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(80);
        gridPane.setVgap(8);

        gridPane.add(new Label(I18N.get("amount")), 0, 0);
        this.amountField = new DoubleField();
        this.amountField.setId("amountTextField");
        gridPane.add(this.amountField, 1, 0);

        gridPane.add(new Label(I18N.get("category")), 0, 1);
        this.categoryComboBox = new JFXComboBox<>();
        this.categoryComboBox.setId("categoryComboBox");
        this.categoryComboBox.setPlaceholder(new Label(I18N.get("selectCategory")));

        gridPane.add(this.categoryComboBox, 1, 1);

        gridPane.add(new Label(I18N.get("product")), 0, 2);
        this.productField = new JFXTextField();
        this.productField.setId("productTextField");
        gridPane.add(this.productField, 1, 2);

        gridPane.add(new Label(I18N.get("purpose")), 0, 3);
        this.purposeField = new JFXTextField();
        this.purposeField.setId("purposeTextField");
        gridPane.add(purposeField, 1, 3);

        gridPane.add(new Label(I18N.get("shop")), 0, 4);
        this.shopField = new JFXTextField();
        this.shopField.setId("shopTextField");
        gridPane.add(this.shopField, 1, 4);

        gridPane.add(new Label(I18N.get("valueDate")), 0, 5);
        this.valueDateField = new DatePicker(new JavaFXFormatter(LocalStorageImpl.getInstance()));
        this.valueDateField.setValue(LocalDate.now());
        this.valueDateField.setId("valueDatePicker");
        gridPane.add(this.valueDateField, 1, 5);

        hBox.getChildren().add(gridPane);

        if (this.getValue() != null) {
            VBox attachmentsContainer = new VBox();
            attachmentsContainer.setSpacing(10);
            attachmentsContainer.setPrefHeight(200);
            attachmentsContainer.getChildren().add(new Label(I18N.get("attachments")));

            GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
            JFXButton uploadAttachmentBtn = new JFXButton(I18N.get("upload"), fontAwesome.create(FontAwesome.Glyph.PLUS));
            uploadAttachmentBtn.setId("uploadAttachmentBtn");
            JFXButton openFileBtn = new JFXButton(I18N.get("openFile"), fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN));
            JFXButton deleteAttachmentBtn = new JFXButton(I18N.get("delete"), fontAwesome.create(FontAwesome.Glyph.TRASH));
            deleteAttachmentBtn.setId("deleteAttachmentBtn");

            uploadAttachmentBtn.setOnAction(event -> {
                FileChooserDialog dialog = new FileChooserDialog();
                dialog.setOnConfirm(this::onUploadAttachment);
            });

            openFileBtn.setOnAction(event -> onOpenAttachment());

            deleteAttachmentBtn.setOnAction(event -> onDeleteAttachment());

            HBox toolBox = new HBox();
            toolBox.setSpacing(8);
            toolBox.getChildren().add(uploadAttachmentBtn);
            toolBox.getChildren().add(openFileBtn);
            toolBox.getChildren().add(deleteAttachmentBtn);

            this.attachmentListView = new JFXListView<>();
            this.attachmentListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Attachment item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            attachmentsContainer.getChildren().add(toolBox);
            attachmentsContainer.getChildren().add(this.attachmentListView);

            hBox.getChildren().add(attachmentsContainer);
        }

        return hBox;
    }

    @Override
    protected double getDialogWidth() {
        return this.getValue() == null ? 400 : 750;
    }

    @Override
    protected void prepareDialogContent() {
        categories.traverse(treeItem -> {
            if (!treeItem.isRoot() && (treeItem.getValue().getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_EXPENSES ||
                    treeItem.getValue().getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_REVENUE)) {
                categoryComboBox.getItems().add((CategoryTree) treeItem);
            }
        });
        this.categoryComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryTree item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(new JavaFXFormatter(LocalStorageImpl.getInstance()).formatCategoryName(item.getValue()));
                } else {
                    setText(null);
                }
            }
        });

        if (this.getValue() != null) {
            this.amountField.setText(String.valueOf(this.getValue().getAmount()));
            this.categoryComboBox.getSelectionModel().select((CategoryTree) TreeUtil.getByValue(this.categories,
                    this.getValue().getCategoryTree(), Comparator.comparingInt(Category::getId)));
            this.productField.setText(this.getValue().getProduct());
            this.purposeField.setText(this.getValue().getPurpose());
            this.shopField.setText(this.getValue().getShop());
            this.valueDateField.setValue(this.getValue().getValueDate());

            if (this.getValue().getAttachments() != null) {
                this.attachmentListView.getItems().addAll(new ArrayList<>(this.getValue().getAttachments()));
            }
        }
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            setErrorMessage(I18N.get("selectCategory"));
            result = false;
        }

        if (Double.valueOf(this.amountField.getText()).equals(0.0)) {
            setErrorMessage(I18N.get("selectValidAmount"));
            result = false;
        }

        return result;
    }

    @Override
    protected void onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new VariableTransaction(0,
                    Double.parseDouble(this.amountField.getText()),
                    this.valueDateField.getValue(),
                    this.categoryComboBox.getSelectionModel().getSelectedItem(),
                    this.productField.getText(),
                    this.purposeField.getText(),
                    this.shopField.getText()));
            this.getValue().getCategoryTree().getTransactions().add(this.getValue());
        } else {
            this.getValue().setAmount(Double.parseDouble(this.amountField.getText()));
            this.getValue().setCategoryTree(this.categoryComboBox.getSelectionModel().getSelectedItem());
            this.getValue().setProduct(this.productField.getText());
            this.getValue().setPurpose(this.purposeField.getText());
            this.getValue().setValueDate(this.valueDateField.getValue());
            this.getValue().setShop(this.shopField.getText());
        }

        super.onConfirm();
    }

    private void onUploadAttachment(File attachmentFile) {
        if (attachmentFile != null) {
            byte[] attachmentContent = new byte[(int) attachmentFile.length()];
            try (BufferedInputStream bufferedReader = new BufferedInputStream(new FileInputStream(attachmentFile))) {
                if (bufferedReader.read(attachmentContent, 0, attachmentContent.length) != -1) {
                    Map<String, Serializable> parameters = new HashMap<>();
                    parameters.put("transaction", this.getValue());
                    parameters.put("attachment", new ContentAttachment(0, this.getValue(),
                            attachmentFile.getName(), LocalDate.now(), attachmentContent));
                    Executors.newCachedThreadPool().execute(new ServerRequestHandler((User) LocalStorageImpl.getInstance().readObject("user"),
                            "uploadTransactionAttachment", parameters, result -> {
                        attachmentListView.getItems().add((Attachment) result.getResult());
                        if (getValue().getAttachments() == null) {
                            getValue().setAttachments(new HashSet<>());
                        }
                        getValue().getAttachments().add((Attachment) result.getResult());
                    }));
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void onOpenAttachment() {
        File file = new File(LocalStorageImpl.LocalStorageFile.TRANSACTIONS.getFile().getParent() +
                "/transactions/" + this.getValue().getId() + "/attachments/" +
                this.attachmentListView.getSelectionModel().getSelectedItem().getName());

        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                new FinancerExceptionDialog("Financer", e);
            }
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("attachmentId", this.attachmentListView.getSelectionModel().getSelectedItem().getId());

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(
                    (User) LocalStorageImpl.getInstance().readObject("user"),
                    "getAttachment", parameters,
                    result -> {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                            fileOutputStream.write(((ContentAttachment) result.getResult()).getContent());
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }));
        }
    }

    private void onDeleteAttachment() {
        FinancerConfirmDialog confirmDialog = new FinancerConfirmDialog(I18N.get("confirmDeleteAttachment"));
        confirmDialog.setOnConfirm(result -> {
            Attachment attachment = this.attachmentListView.getSelectionModel().getSelectedItem();

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("attachmentId", attachment.getId());

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(
                    (User) LocalStorageImpl.getInstance().readObject("user"), "deleteAttachment",
                    parameters, result1 -> {
                getValue().getAttachments().remove(attachment);
                File file = new File(LocalStorageImpl.LocalStorageFile.TRANSACTIONS.getFile().getParent() +
                        "/transactions/" + attachmentListView.getSelectionModel().getSelectedItem().getTransaction().getId() +
                        "/attachments/" + attachmentListView.getSelectionModel().getSelectedItem().getName());
                try {
                    if (file.exists()) {
                        Files.delete(file.toPath());
                    }
                    Platform.runLater(() -> attachmentListView.getItems().remove(attachmentListView.getSelectionModel().getSelectedItem()));
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Platform.runLater(() -> new FinancerExceptionDialog("Financer", new IOException("File could not be deleted")));
                }
            }));
        });
    }
}