package com.example.governorsindhfaculty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.governorsindhfaculty.model.NewsModel;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UploadNews extends AppCompatActivity {

    private TextView textSelectedDay;
    private EditText editTextNewsTitle, editTextNewsContent;
    private Button buttonUploadNews;

    private FirebaseFirestore firestore;
    private String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_news);

        textSelectedDay = findViewById(R.id.text_selected_day);
        editTextNewsTitle = findViewById(R.id.edit_text_news_title);
        editTextNewsContent = findViewById(R.id.edit_text_news_content);
        buttonUploadNews = findViewById(R.id.button_upload_news);

        firestore = FirebaseFirestore.getInstance();

        selectedDay = getIntent().getStringExtra("selectedDay");
        textSelectedDay.setText("Selected Day: " + selectedDay);

        buttonUploadNews.setOnClickListener(v -> uploadNews());
    }

    private void uploadNews() {
        String title = editTextNewsTitle.getText().toString().trim();
        String content = editTextNewsContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading News...");
        pd.show();

        NewsModel news = new NewsModel(selectedDay, title, content);

        // Create a map to store the news data
        Map<String, Object> newsMap = new HashMap<>();
        newsMap.put("day", selectedDay);
        newsMap.put("title", title);
        newsMap.put("content", content);
        newsMap.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("news").add(newsMap)
                .addOnSuccessListener(documentReference -> {
                    // Also add the news data to the "updates" collection
                    Map<String, Object> updatesMap = new HashMap<>();
                    updatesMap.put("day", selectedDay);
                    updatesMap.put("title", title);
                    updatesMap.put("content", content);
                    updatesMap.put("type", "news");
                    updatesMap.put("timestamp", FieldValue.serverTimestamp());

                    firestore.collection("updates").add(updatesMap)
                            .addOnSuccessListener(updateDocRef -> {
                                pd.dismiss();
                                Toast.makeText(this, "News Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                                editTextNewsTitle.setText("");
                                editTextNewsContent.setText("");
                            })
                            .addOnFailureListener(e -> {
                                pd.dismiss();
                                Toast.makeText(this, "Failed to Upload News to Updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Failed to Upload News: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}
