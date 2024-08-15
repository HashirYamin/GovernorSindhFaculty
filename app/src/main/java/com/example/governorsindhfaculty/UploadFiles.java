package com.example.governorsindhfaculty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadFiles extends AppCompatActivity {

    private EditText selectFile;
    private Button uploadBtn;

    StorageReference storageReference;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        selectFile = findViewById(R.id.edit_text_select_files);
        uploadBtn = findViewById(R.id.upload_btn);

        uploadBtn.setEnabled(false);

        selectFile.setOnClickListener(view -> selectFile());
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select files"), 101);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            uploadBtn.setEnabled(true);
            selectFile.setText(displayName);

            uploadBtn.setOnClickListener(view -> uploadFile(data.getData()));
        }
    }

    private void uploadFile(Uri data) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("File Uploading...");
        pd.show();

        String fileExtension = getFileExtension(data);
        final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + "." + fileExtension);
        reference.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> fileMap = new HashMap<>();
                        fileMap.put("fileName", selectFile.getText().toString());
                        fileMap.put("fileUrl", uri.toString());
                        fileMap.put("type", "file");
                        fileMap.put("timestamp", FieldValue.serverTimestamp());


                        firestore.collection("files").add(fileMap)
                                .addOnSuccessListener(documentReference -> {
                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put("fileName", selectFile.getText().toString());
                                    updateMap.put("fileUrl", uri.toString());
                                    updateMap.put("type", "file");
                                    updateMap.put("timestamp", FieldValue.serverTimestamp());

                                    firestore.collection("updates").add(updateMap)
                                            .addOnSuccessListener(documentReference1 -> {
                                                Toast.makeText(UploadFiles.this, "File Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                                selectFile.setText("");
                                            })
                                            .addOnFailureListener(e -> {
                                                pd.dismiss();
                                                Toast.makeText(UploadFiles.this, "File Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    pd.dismiss();
                                    Toast.makeText(UploadFiles.this, "File Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnProgressListener(snapshot -> {
                    float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    pd.setMessage("Uploaded: " + (int) percent + "%");
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(UploadFiles.this, "File Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri uri) {
        String extension;
        extension = this.getContentResolver().getType(uri);
        if (extension == null) {
            String uriString = uri.toString();
            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
        }
        return extension;
    }
}
