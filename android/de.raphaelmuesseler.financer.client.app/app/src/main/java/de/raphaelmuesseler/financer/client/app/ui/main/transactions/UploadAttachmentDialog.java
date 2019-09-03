package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.LocalDate;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.transactions.ContentAttachment;
import de.raphaelmuesseler.financer.util.collections.Action;

import static android.app.Activity.RESULT_OK;
import static de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity.REQUEST_WRITE_STORAGE_PERMISSION;

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
                            attachment.setName(new File(data.getData().getPath()).getName());
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