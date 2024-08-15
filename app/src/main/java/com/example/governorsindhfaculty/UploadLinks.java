package com.example.governorsindhfaculty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UploadLinks extends AppCompatActivity {
    private EditText editTextLinkDescription, editTextLinkUrl;
    private Button buttonUploadLink;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_links);

        editTextLinkDescription = findViewById(R.id.edit_text_description);
        editTextLinkUrl = findViewById(R.id.edit_text_links);
        buttonUploadLink = findViewById(R.id.upload_link_btn);

        firestore = FirebaseFirestore.getInstance();

        buttonUploadLink.setOnClickListener(v -> uploadLink());
    }

    private void uploadLink() {
        String linkDescription = editTextLinkDescription.getText().toString().trim();
        String linkUrl = editTextLinkUrl.getText().toString().trim();

        if (linkDescription.isEmpty() || linkUrl.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidUrl(linkUrl)) {
            Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Link...");
        pd.show();

        // Create a map to store the link data
        Map<String, Object> linkMap = new HashMap<>();
        linkMap.put("linkDescription", linkDescription);
        linkMap.put("linkUrl", linkUrl);
        linkMap.put("timestamp", FieldValue.serverTimestamp());  // Add this line to store the timestamp

        firestore.collection("links").add(linkMap)
                .addOnSuccessListener(documentReference -> {
                    // Also add the link data to the "updates" collection
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("url", linkUrl);
                    updateMap.put("description", linkDescription);
                    updateMap.put("type", "link");
                    updateMap.put("timestamp", FieldValue.serverTimestamp());

                    firestore.collection("updates").add(updateMap)
                            .addOnSuccessListener(updateDocRef -> {
                                pd.dismiss();
                                Toast.makeText(UploadLinks.this, "Link Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                                editTextLinkDescription.setText("");
                                editTextLinkUrl.setText("");
                            })
                            .addOnFailureListener(e -> {
                                pd.dismiss();
                                Toast.makeText(UploadLinks.this, "Failed to Upload Link to Updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(UploadLinks.this, "Failed to Upload Link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private boolean isValidUrl(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }
}
