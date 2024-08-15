package com.example.governorsindhfaculty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class YoutubeVedios extends AppCompatActivity {

    private EditText editTextVideoDescription, editTextVideoUrl;
    private Button buttonUploadVideo;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_vedios);

        editTextVideoDescription = findViewById(R.id.edit_text_yt_description);
        editTextVideoUrl = findViewById(R.id.edit_text_yt_links);
        buttonUploadVideo = findViewById(R.id.upload_vedio);

        firestore = FirebaseFirestore.getInstance();

        buttonUploadVideo.setOnClickListener(v -> uploadVideo());
    }

    private void uploadVideo() {
        String videoDescription = editTextVideoDescription.getText().toString().trim();
        String videoUrl = editTextVideoUrl.getText().toString().trim();

        if (videoDescription.isEmpty() || videoUrl.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidUrl(videoUrl)) {
            Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Video...");
        pd.show();

        // Create a map to store the video data
        Map<String, Object> videoMap = new HashMap<>();
        videoMap.put("videoDescription", videoDescription);
        videoMap.put("videoUrl", videoUrl);
        videoMap.put("timestamp", FieldValue.serverTimestamp());  // Add this line to store the timestamp

        firestore.collection("youtubeVideos").add(videoMap)
                .addOnSuccessListener(documentReference -> {
                    // Also add the video data to the "updates" collection
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("url", videoUrl);
                    updateMap.put("description", videoDescription);
                    updateMap.put("type", "video");
                    updateMap.put("timestamp", FieldValue.serverTimestamp());

                    firestore.collection("updates").add(updateMap)
                            .addOnSuccessListener(updateDocRef -> {
                                pd.dismiss();
                                Toast.makeText(YoutubeVedios.this, "Video Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                                editTextVideoDescription.setText("");
                                editTextVideoUrl.setText("");
                            })
                            .addOnFailureListener(e -> {
                                pd.dismiss();
                                Toast.makeText(YoutubeVedios.this, "Failed to Upload Video to Updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(YoutubeVedios.this, "Failed to Upload Video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidUrl(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }
}
