package org.financer.client.app.ui.main.transactions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import org.financer.client.app.R;
import org.financer.client.app.format.AndroidFormatter;
import org.financer.client.app.local.LocalStorageImpl;
import org.financer.shared.model.transactions.ContentAttachment;
import org.financer.util.collections.Action;

import static android.app.Activity.RESULT_OK;
import static org.financer.client.app.ui.main.FinancerActivity.REQUEST_WRITE_STORAGE_PERMISSION;

public class UploadAttachmentDialog extends BottomSheetDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CHOOSE_FILE = 2;

    private Action<ContentAttachment> action;

    public UploadAttachmentDialog() {
        // empty constructor
    }

    public static UploadAttachmentDialog newInstance() {
        Bundle bundle = new Bundle();

        UploadAttachmentDialog fragment = new UploadAttachmentDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_upload_attachment, container,
                false);

        LinearLayout takePhotoBtn = view.findViewById(R.id.btn_attachment_open_camera);
        LinearLayout chooseFileBtn = view.findViewById(R.id.btn_attachment_choose_file);

        takePhotoBtn.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        chooseFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file)), REQUEST_CHOOSE_FILE);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentAttachment attachment = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    if (image != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] content = stream.toByteArray();
                        image.recycle();

                        attachment = new ContentAttachment();
                        attachment.setContent(content);
                        attachment.setName(new AndroidFormatter(LocalStorageImpl.getInstance(), getContext())
                                .formatDate(LocalDate.now()) + ".jpg");
                        attachment.setUploadDate(LocalDate.now());
                    }
                    break;
                case REQUEST_CHOOSE_FILE:
                    boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE_PERMISSION);
                    } else {
                        if (data != null && data.getData() != null) {
                            byte[] content = getBytes(data.getData());

                            attachment = new ContentAttachment();
                            attachment.setContent(content);
                            attachment.setName(getFileName(data.getData()));
                            attachment.setUploadDate(LocalDate.now());
                        }
                    }

                    break;
            }
        }

        if (attachment != null && attachment.getContent() != null && attachment.getContent().length > 0) {
            this.action.action(attachment);
        }
    }

    public void setOnSubmit(Action<ContentAttachment> action) {
        this.action = action;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private byte[] getBytes(@NonNull Uri uri) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri)) {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}