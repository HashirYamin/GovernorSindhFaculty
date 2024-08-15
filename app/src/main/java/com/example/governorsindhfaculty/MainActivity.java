package com.example.governorsindhfaculty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    CardView uploadFiles, attach_links, attachYtVedios, assignments, news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadFiles = findViewById(R.id.upload_files);
        attach_links = findViewById(R.id.attach_links);
        attachYtVedios = findViewById(R.id.upload_vedios);
        assignments = findViewById(R.id.upload_assignments);
        news = findViewById(R.id.news);

        uploadFiles.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,UploadFiles.class);
            startActivity(intent);
        });
        attach_links.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,UploadLinks.class);
            startActivity(intent);
        });
        attachYtVedios.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,YoutubeVedios.class);
            startActivity(intent);
        });
        assignments.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,UploadAssignments.class);
            startActivity(intent);
        });
        news.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,SelectDayActivity.class);
            startActivity(intent);
        });
    }
}