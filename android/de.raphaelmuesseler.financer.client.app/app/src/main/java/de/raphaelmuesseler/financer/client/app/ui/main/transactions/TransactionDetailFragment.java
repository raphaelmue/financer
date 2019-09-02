package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Attachment;
import de.raphaelmuesseler.financer.shared.model.transactions.ContentAttachment;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import static android.app.Activity.RESULT_OK;
import static de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity.REQUEST_WRITE_STORAGE_PERMISSION;

public class TransactionDetailFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_EDIT_TRANSACTION = 2;
    private Toolbar toolbar;
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
        amountTextView = view.findViewById(R.id.tv_transaction_amount);
        productTextView = view.findViewById(R.id.tv_transaction_product);
        valueDateTextView = view.findViewById(R.id.tv_transaction_value_date);
        categoryTextView = view.findViewById(R.id.tv_transaction_category);
        purposeTextView = view.findViewById(R.id.tv_transaction_purpose);
        shopTextView = view.findViewById(R.id.tv_transaction_shop);
        ImageButton editTransactionBtn = view.findViewById(R.id.btn_edit_transaction);
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
        this.attachments.addAll(transaction.getAttachments());
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            ((AttachmentListViewAdapter) attachmentListView.getAdapter()).notifyDataSetChanged();
            ((AttachmentListViewAdapter) attachmentListView.getAdapter()).setListViewHeightBasedOnChildren(attachmentListView);
        });
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

    private void openAttachment(Attachment attachment) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION);
        } else {
            File directory = new File(Environment.getExternalStorageDirectory() + "/Financer/transactions/"
                    + attachment.getTransaction().getId());
            directory.mkdirs();
            File file = new File(directory, attachment.getName());
            if (file.exists()) {
                openAttachmentFile(file);
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

                            getActivity().runOnUiThread(() -> openAttachmentFile(file));
                        }));
            }
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
