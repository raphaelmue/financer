package org.financer.client.javafx.main.transactions

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXTextField
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.transaction.Attachment
import org.financer.client.domain.model.transaction.VariableTransaction
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DatePicker
import org.financer.client.javafx.components.DoubleField
import org.financer.client.javafx.dialogs.FileChooserDialog
import org.financer.client.javafx.dialogs.FinancerConfirmDialog
import org.financer.client.javafx.dialogs.FinancerDialog
import org.financer.client.javafx.dialogs.FinancerExceptionDialog
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.ValueDate
import org.financer.util.collections.Action
import org.financer.util.collections.Tree
import org.financer.util.collections.TreeUtil
import java.awt.Desktop
import java.io.*
import java.nio.file.Files
import java.time.LocalDate
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

internal class TransactionDialog(transaction: VariableTransaction?, private val categories: CategoryRoot?) : FinancerDialog<VariableTransaction?>(transaction) {
    private var amountField: DoubleField? = null
    private var categoryComboBox: JFXComboBox<Category>? = null
    private var productField: JFXTextField? = null
    private var descriptionField: JFXTextField? = null
    private var vendorField: JFXTextField? = null
    private var valueDateField: DatePicker? = null
    private var attachmentListView: JFXListView<Attachment>? = null
    private val restApi: RestApi = RestApiImpl()
    private val logger = Logger.getLogger("FinancerApplication")
    override val dialogContent: Region
        get() {
            val hBox = HBox()
            hBox.spacing = 30.0
            val gridPane = GridPane()
            gridPane.hgap = 80.0
            gridPane.vgap = 8.0
            gridPane.add(Label(get("amount")), 0, 0)
            amountField = DoubleField()
            amountField!!.id = "amountTextField"
            gridPane.add(amountField, 1, 0)
            gridPane.add(Label(get("category")), 0, 1)
            categoryComboBox = JFXComboBox()
            categoryComboBox!!.id = "categoryComboBox"
            categoryComboBox!!.placeholder = Label(get("selectCategory"))
            gridPane.add(categoryComboBox, 1, 1)
            gridPane.add(Label(get("product")), 0, 2)
            productField = JFXTextField()
            productField!!.id = "productTextField"
            gridPane.add(productField, 1, 2)
            gridPane.add(Label(get("purpose")), 0, 3)
            descriptionField = JFXTextField()
            descriptionField!!.id = "purposeTextField"
            gridPane.add(descriptionField, 1, 3)
            gridPane.add(Label(get("shop")), 0, 4)
            vendorField = JFXTextField()
            vendorField!!.id = "shopTextField"
            gridPane.add(vendorField, 1, 4)
            gridPane.add(Label(get("valueDate")), 0, 5)
            valueDateField = DatePicker(JavaFXFormatter(LocalStorageImpl.getInstance()))
            valueDateField!!.value = LocalDate.now()
            valueDateField!!.id = "valueDatePicker"
            gridPane.add(valueDateField, 1, 5)
            hBox.children.add(gridPane)
            if (value != null) {
                val attachmentsContainer = VBox()
                attachmentsContainer.spacing = 10.0
                attachmentsContainer.prefHeight = 200.0
                attachmentsContainer.children.add(Label(get("attachments")))
                val fontAwesome = GlyphFontRegistry.font("FontAwesome")
                val uploadAttachmentBtn = JFXButton(get("upload"), fontAwesome.create(FontAwesome.Glyph.PLUS))
                uploadAttachmentBtn.id = "uploadAttachmentBtn"
                val openFileBtn = JFXButton(get("openFile"), fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN))
                val deleteAttachmentBtn = JFXButton(get("delete"), fontAwesome.create(FontAwesome.Glyph.TRASH))
                deleteAttachmentBtn.id = "deleteAttachmentBtn"
                uploadAttachmentBtn.onAction = EventHandler { event: ActionEvent? ->
                    val dialog = FileChooserDialog()
                    dialog.setOnConfirm(Action { attachmentFile: File? -> onUploadAttachment(attachmentFile) })
                }
                openFileBtn.onAction = EventHandler { event: ActionEvent? -> onOpenAttachment() }
                deleteAttachmentBtn.onAction = EventHandler { event: ActionEvent? -> onDeleteAttachment() }
                val toolBox = HBox()
                toolBox.spacing = 8.0
                toolBox.children.add(uploadAttachmentBtn)
                toolBox.children.add(openFileBtn)
                toolBox.children.add(deleteAttachmentBtn)
                attachmentListView = JFXListView()
                attachmentListView!!.setCellFactory {
                    object : ListCell<Attachment>() {
                        override fun updateItem(item: Attachment, empty: Boolean) {
                            super.updateItem(item, empty)
                            if (empty) {
                                graphic = null
                            } else {
                                text = item.name
                            }
                        }
                    }
                }
                attachmentsContainer.children.add(toolBox)
                attachmentsContainer.children.add(attachmentListView)
                hBox.children.add(attachmentsContainer)
            }
            return hBox
        }

    override val dialogWidth: Double
        get() = (if (value == null) 400 else 750).toDouble()

    override fun prepareDialogContent() {
        categories!!.traverse { treeItem: Tree ->
            val category = treeItem as Category
            if (!category.isRoot && !category.categoryClass!!.isFixed) {
                categoryComboBox!!.items.add(category)
            }
        }
        categoryComboBox!!.setCellFactory {
            object : ListCell<Category>() {
                override fun updateItem(item: Category, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (!empty) {
                        JavaFXFormatter(LocalStorageImpl.getInstance()).format(item)
                    } else {
                        null
                    }
                }
            }
        }
        if (value != null) {
            amountField!!.text = value!!.amount.toString()
            categoryComboBox!!.selectionModel.select(TreeUtil.getByValue(categories,
                    value!!.category, compareBy { t: Category -> t.id }) as Category)
            //            this.productField.setText(this.getValue().getDe());
            descriptionField!!.text = value!!.description
            vendorField!!.text = value!!.vendor
            valueDateField!!.value = value!!.valueDate!!.date
            attachmentListView!!.items.addAll(ArrayList(value!!.getAttachments()))
        }
    }

    override fun checkConsistency(): Boolean {
        var result = true
        if (categoryComboBox!!.selectionModel.selectedItem == null) {
            errorMessage = get("selectCategory")
            result = false
        }
        if (java.lang.Double.valueOf(amountField!!.text) == 0.0) {
            errorMessage = get("selectValidAmount")
            result = false
        }
        return result
    }

    override fun onConfirm() {
        if (value == null) {
            value = VariableTransaction()
                    .setId(0) //                    .setAmount(Double.parseDouble(this.amountField.getText()))
                    .setValueDate(ValueDate(valueDateField!!.value))
                    .setCategory(categoryComboBox!!.selectionModel.selectedItem)
                    .setDescription(descriptionField!!.text)
                    .setVendor(vendorField!!.text)
            value!!.category!!.transactions.add(value!!)
        } else {
//            this.getValue().setAmount(new Amount(Double.parseDouble(this.amountField.getText()))=;
            value!!.setCategory(categoryComboBox!!.selectionModel.selectedItem)
            value!!.setValueDate(ValueDate(valueDateField!!.value))
            value!!.setDescription(descriptionField!!.text)
            value!!.setVendor(vendorField!!.text)
        }
        super.onConfirm()
    }

    private fun onUploadAttachment(attachmentFile: File?) {
        if (attachmentFile != null) {
            val attachmentContent = ByteArray(attachmentFile.length().toInt())
            try {
                BufferedInputStream(FileInputStream(attachmentFile)).use { bufferedReader ->
                    if (bufferedReader.read(attachmentContent, 0, attachmentContent.size) != -1) {
                        val attachment = Attachment()
                                .setName(attachmentFile.name)
                                .setUploadDate(LocalDate.now())
                                .setContent(attachmentContent)
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = restApi.createAttachment(value!!.id, attachment)
                            if (result != null) {
                                attachmentListView!!.items.add(result)
                                value!!.getAttachments().add(result)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                logger.log(Level.SEVERE, e.message, e)
            }
        }
    }

    private fun onOpenAttachment() {
        val file = File(LocalStorageImpl.LocalStorageFile.TRANSACTIONS.file.parent +
                "/transactions/" + value!!.id + "/attachments/" +
                attachmentListView!!.selectionModel.selectedItem.name)
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file)
            } catch (e: IOException) {
                FinancerExceptionDialog("Financer", e)
            }
        } else {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val parameters: MutableMap<String, Serializable> = HashMap()
            parameters["attachmentId"] = attachmentListView!!.selectionModel.selectedItem.id
            CoroutineScope(Dispatchers.IO).launch {
                val result = restApi.getAttachment(value!!.id, attachmentListView!!.selectionModel.selectedItem.id)
                if (result != null) {
                    try {
                        FileOutputStream(file).use { fileOutputStream ->
                            fileOutputStream.write(result.content)
                            Desktop.getDesktop().open(file)
                        }
                    } catch (e: IOException) {
                        logger.log(Level.SEVERE, e.message, e)
                    }
                }
            }
        }
    }

    private fun onDeleteAttachment() {
        val confirmDialog = FinancerConfirmDialog(get("confirmDeleteAttachment"))
        confirmDialog.setOnConfirm(Action { result: Boolean? ->
            val attachment = attachmentListView!!.selectionModel.selectedItem
            CoroutineScope(Dispatchers.IO).launch {
                restApi.deleteAttachment(value!!.id, attachment.id)
                value!!.getAttachments().remove(attachment)
                val file = File(LocalStorageImpl.LocalStorageFile.TRANSACTIONS.file.parent +
                        "/transactions/" + attachmentListView!!.selectionModel.selectedItem.transaction!!.id +
                        "/attachments/" + attachmentListView!!.selectionModel.selectedItem.name)
                try {
                    if (file.exists()) {
                        Files.delete(file.toPath())
                    }
                    Platform.runLater { attachmentListView!!.items.remove(attachmentListView!!.selectionModel.selectedItem) }
                } catch (e: IOException) {
                    logger.log(Level.SEVERE, e.message, e)
                    Platform.runLater { FinancerExceptionDialog("Financer", IOException("File could not be deleted")) }
                }
            }
        })
    }

    init {
        prepareDialogContent()
        setDialogTitle(get("transaction"))
    }
}