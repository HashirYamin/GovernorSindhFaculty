package com.example.governorsindhfaculty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectDayActivity extends AppCompatActivity {
    private RadioGroup radioGroupDays;
    private Button buttonNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_day);
        radioGroupDays = findViewById(R.id.radio_group_days);
        buttonNext = findViewById(R.id.button_next);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupDays.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedDay = selectedRadioButton.getText().toString();

                    Intent intent = new Intent(SelectDayActivity.this, UploadNews.class);
                    intent.putExtra("selectedDay", selectedDay);
                    startActivity(intent);
                } else {
                    Toast.makeText(SelectDayActivity.this, "Please select a day", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}