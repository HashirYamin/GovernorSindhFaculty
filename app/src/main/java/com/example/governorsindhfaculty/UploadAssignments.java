package com.example.governorsindhfaculty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UploadAssignments extends AppCompatActivity {
    private EditText editTextTitle, editTextDescription, editTextLinkOrFile;
    private Button buttonUploadAssignment;

    private StorageReference storageReference;
    private FirebaseFirestore firestore;

    private static final int PICK_FILE_REQUEST = 101;
    private Uri fileUri;
    private String selectedFileType;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_assignments);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextLinkOrFile = findViewById(R.id.edit_text_link_or_file);
        buttonUploadAssignment = findViewById(R.id.button_upload_assignment);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        editTextLinkOrFile.setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;
            private static final int DOUBLE_CLICK_TIME_DELTA = 300; // milliseconds

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long clickTime = System.currentTimeMillis();
                    if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        selectFile();
                        return true;
                    }
                    lastClickTime = clickTime;
                }
                return false;
            }
        });

        editTextLinkOrFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith("http://") || s.toString().startsWith("https://")) {
                    fileUri = null; // Reset fileUri if it's a link
                }
            }
        });

        buttonUploadAssignment.setOnClickListener(view -> uploadAssignment());
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String uriString = fileUri.toString();
            if (uriString.endsWith(".pdf")) {
                selectedFileType = "pdf";
            } else if (uriString.endsWith(".doc") || uriString.endsWith(".docx")) {
                selectedFileType = "word";
            } else {
                Toast.makeText(this, "Please select a valid PDF or Word file", Toast.LENGTH_SHORT).show();
                return;
            }
            editTextLinkOrFile.setText(uriString.substring(uriString.lastIndexOf('/') + 1));
        }
    }

    private void uploadAssignment() {
        final String title = editTextTitle.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String linkOrFile = editTextLinkOrFile.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || linkOrFile.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog only for file uploads
        ProgressDialog progressDialog;
        if (fileUri != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
        } else {
            progressDialog = null;
        }

        if (fileUri != null) { // File upload
            final StorageReference fileRef = storageReference.child("assignments/" + System.currentTimeMillis() + "." + selectedFileType);
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveAssignmentToFirestore(title, description, downloadUrl, "file", progressDialog);
                    }))
                    .addOnFailureListener(e -> {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(UploadAssignments.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else { // Link upload
            saveAssignmentToFirestore(title, description, linkOrFile, "link", null);
        }
    }

    private void saveAssignmentToFirestore(String title, String description, String linkOrFile, String type, ProgressDialog progressDialog) {
        // Create the assignment data
        Map<String, Object> assignment = new HashMap<>();
        assignment.put("title", title);
        assignment.put("description", description);
        assignment.put("linkOrFile", linkOrFile);
        assignment.put("type", type);
        assignment.put("timestamp", FieldValue.serverTimestamp());

        // Save the assignment to Firestore
        firestore.collection("assignments").add(assignment)
                .addOnSuccessListener(documentReference -> {
                    // Prepare the update data
                    Map<String, Object> updatesMap = new HashMap<>();
                    updatesMap.put("type", "assignment");
                    updatesMap.put("assignmentId", documentReference.getId()); // Save the assignment document ID
                    updatesMap.put("title", title);  // Include title for display in updates
                    updatesMap.put("description", description); // Include description
                    updatesMap.put("timestamp", FieldValue.serverTimestamp());

                    // Save the update information to the "updates" collection
                    firestore.collection("updates").add(updatesMap)
                            .addOnSuccessListener(documentReference1 -> {
                                Toast.makeText(UploadAssignments.this, "Assignment uploaded successfully", Toast.LENGTH_SHORT).show();
                                clearFields();
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(UploadAssignments.this, "Failed to update updates collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UploadAssignments.this, "Failed to upload assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        editTextTitle.setText("");           // Clear the title field
        editTextDescription.setText("");     // Clear the description field
        editTextLinkOrFile.setText("");      // Clear the link or file field
        fileUri = null;                      // Reset the fileUri to null
        selectedFileType = null;             // Reset the selected file type to null
    }
}
