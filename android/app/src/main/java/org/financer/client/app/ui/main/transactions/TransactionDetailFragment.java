package org.financer.client.app.ui.main.transactions;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.financer.client.app.R;
import org.financer.client.app.format.AndroidFormatter;
import org.financer.client.app.local.LocalStorageImpl;
import org.financer.client.app.ui.main.FinancerActivity;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.format.Formatter;
import org.financer.client.local.Application;
import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.categories.CategoryTree;
import org.financer.shared.model.transactions.Attachment;
import org.financer.shared.model.transactions.ContentAttachment;
import org.financer.shared.model.transactions.VariableTransaction;
import org.financer.shared.model.user.User;
import org.financer.util.collections.Action;
import org.financer.util.collections.TreeUtil;
import org.financer.util.concurrency.FinancerExecutor;

import static android.app.Activity.RESULT_OK;
import static org.financer.client.app.ui.main.FinancerActivity.REQUEST_WRITE_STORAGE_PERMISSION;

public class TransactionDetailFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_EDIT_TRANSACTION = 2;
    private Toolbar toolbar;
    ImageView headerImageView;
    TextView amountTextView;
    TextView productTextView;
    TextView valueDateTextView;
    TextView categoryTextView;
    TextView purposeTextView;
    TextView shopTextView;
    ListView attachmentListView;

    private final List<Attachment> attachments = new ArrayList<>();

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), getContext());
    private final User user = (User) LocalStorageImpl.getInstance().readObject("user");


    private Action<Void> cancelAction;


    public TransactionDetailFragment() {
        // requires empty constructor
    }

    public static TransactionDetailFragment newInstance(VariableTransaction variableTransaction) {

        Bundle bundle = new Bundle();

        TransactionDetailFragment fragment = new TransactionDetailFragment();
        bundle.putSerializable("transaction", variableTransaction);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container,
                false);

        FloatingActionButton uploadAttachmentBtn = view.findViewById(R.id.btn_upload_attachment);
        uploadAttachmentBtn.setOnClickListener(v -> {
            UploadAttachmentDialog uploadAttachmentDialog = UploadAttachmentDialog.newInstance();
            uploadAttachmentDialog.show(getFragmentManager(), uploadAttachmentDialog.getTag());
            uploadAttachmentDialog.setOnSubmit(this::uploadAttachment);
        });
        toolbar = view.findViewById(R.id.toolbar_transaction_details);
        headerImageView = view.findViewById(R.id.iv_transaction_detail_image);
        amountTextView = view.findViewById(R.id.tv_transaction_amount);
        productTextView = view.findViewById(R.id.tv_transaction_product);
        valueDateTextView = view.findViewById(R.id.tv_transaction_value_date);
        categoryTextView = view.findViewById(R.id.tv_transaction_category);
        purposeTextView = view.findViewById(R.id.tv_transaction_purpose);
        shopTextView = view.findViewById(R.id.tv_transaction_shop);

        ImageButton editTransactionBtn = view.findViewById(R.id.btn_edit_transaction);
        ImageButton deleteTransactionBtn = view.findViewById(R.id.btn_delete_transaction);
        attachmentListView = view.findViewById(R.id.lv_transaction_attachments);
        attachmentListView.setEmptyView(view.findViewById(R.id.tv_no_attachments));
        attachmentListView.setAdapter(new AttachmentListViewAdapter(getContext(), this.attachments));
        attachmentListView.setOnItemClickListener((parent, view1, position, id) ->
                this.openAttachment((Attachment) attachmentListView.getItemAtPosition(position)));

        if (getArguments() != null && !getArguments().isEmpty() && getArguments().getSerializable("transaction") != null) {
            final VariableTransaction transaction = (VariableTransaction) getArguments().getSerializable("transaction");

            this.fillLabels(transaction);
            editTransactionBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), TransactionActivity.class);
                VariableTransaction _transaction = (VariableTransaction) getArguments().getSerializable("transaction");
                intent.putExtra("transaction", _transaction);
                startActivityForResult(intent, REQUEST_EDIT_TRANSACTION);
            });

            deleteTransactionBtn.setOnClickListener(v -> {
                AtomicBoolean canceled = new AtomicBoolean(false);
                Snackbar snackbar = Snackbar.make(view, R.string.deleted_transaction, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.undo, v1 -> canceled.set(true));
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (!canceled.get()) {
                            deleteTransaction(transaction);
                        }
                    }
                });
                snackbar.show();
                getActivity().runOnUiThread(this::dismiss);
            });
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        return dialog;
    }

    private void fillLabels(VariableTransaction transaction) {
        toolbar.setTitle(transaction.getProduct());
        amountTextView.setText(formatter.formatCurrency(transaction.getAmount()));
        productTextView.setText(transaction.getProduct());
        valueDateTextView.setText(formatter.formatDate(transaction.getValueDate()));
        categoryTextView.setText(formatter.formatCategoryName(transaction.getCategoryTree()));
        purposeTextView.setText(transaction.getPurpose());
        shopTextView.setText(transaction.getShop());

        this.attachments.clear();
        if (transaction.getAttachments() != null && !transaction.getAttachments().isEmpty()) {
            this.attachments.addAll(transaction.getAttachments());
            this.attachments.sort((o1, o2) -> o2.getUploadDate().compareTo(o1.getUploadDate()));
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                ((AttachmentListViewAdapter) attachmentListView.getAdapter()).notifyDataSetChanged();
                ((AttachmentListViewAdapter) attachmentListView.getAdapter()).setListViewHeightBasedOnChildren(attachmentListView);
            });

            for (Attachment attachment : this.attachments) {
                if (attachment.getName().matches(".*([.](jpg|png|jpeg))")) {
                    loadAttachment(attachment, file -> getActivity().runOnUiThread(() -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        headerImageView.setImageBitmap(bitmap);
                    }));
                    break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_TRANSACTION) {
            if (resultCode == RESULT_OK) {
                VariableTransaction transaction = (VariableTransaction) data.getSerializableExtra("variableTransaction");
                transaction.getCategoryTree().getValue().setUser(user);

                Map<String, Serializable> parameters = new HashMap<>();
                parameters.put("variableTransaction", transaction);

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "updateTransaction", parameters,
                        connectionResult -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                            CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(), (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                            categoryTree.getTransactions().remove(transaction);
                            categoryTree.getTransactions().add(transaction);
                            transaction.setCategoryTree(categoryTree);
                            LocalStorageImpl.getInstance().writeObject("categories", baseCategory);

                            FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS,
                                    getString(R.string.success_updated_transaction));
                            fillLabels(transaction);
                            getArguments().remove("transaction");
                            getArguments().putSerializable("transaction", transaction);
                        })));
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.cancelAction.action(null);
    }

    public void setOnCancelListener(Action<Void> action) {
        this.cancelAction = action;
    }

    private void deleteTransaction(VariableTransaction transaction) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("variableTransactionId", transaction.getId());

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "deleteTransaction", parameters, connectionResult ->
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                    CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(),
                            (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                    categoryTree.getTransactions().remove(transaction);
                    LocalStorageImpl.getInstance().writeObject("categories", baseCategory);

                    dismiss();
                    FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS,
                            getString(R.string.success_deleted_transaction));
                })));
    }

    private void openAttachment(Attachment attachment) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION);
        } else {
            loadAttachment(attachment, file -> getActivity().runOnUiThread(() -> openAttachmentFile(file)));
        }
    }

    private void loadAttachment(Attachment attachment, Action<File> action) {
        File directory = new File(Environment.getExternalStorageDirectory() + "/Financer/transactions/"
                + attachment.getTransaction().getId());
        directory.mkdirs();
        File file = new File(directory, attachment.getName());
        if (file.exists()) {
            action.action(file);
        } else {
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("attachmentId", attachment.getId());
            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getAttachment", parameters,
                    connectionResult -> {
                        ContentAttachment contentAttachment = (ContentAttachment) connectionResult.getResult();
                        try (FileOutputStream outputStream = new FileOutputStream(file)) {
                            outputStream.write(contentAttachment.getContent());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        action.action(file);
                    }));
        }
    }

    private void openAttachmentFile(File file) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(getContext(),
                getActivity().getApplicationContext()
                        .getPackageName() + ".provider", file),
                URLConnection.guessContentTypeFromName(file.getName()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void uploadAttachment(@NonNull ContentAttachment contentAttachment) {
        final VariableTransaction transaction = (VariableTransaction) getArguments().getSerializable("transaction");

        if (transaction != null) {
            contentAttachment.setTransaction(transaction);

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("transaction", transaction);
            parameters.put("attachment", contentAttachment);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "uploadTransactionAttachment", parameters,
                    connectionResult -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        Attachment result = (Attachment) connectionResult.getResult();
                        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                        CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(),
                                (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                        categoryTree.getTransactions().remove(transaction);
                        transaction.getAttachments().add(result);
                        categoryTree.getTransactions().add(transaction);
                        LocalStorageImpl.getInstance().writeObject("categories", baseCategory);

                        FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS,
                                getString(R.string.success_uploaded_attachment));
                        fillLabels(transaction);
                        getArguments().remove("transaction");
                        getArguments().putSerializable("transaction", transaction);
                    })));
        }
    }
}
