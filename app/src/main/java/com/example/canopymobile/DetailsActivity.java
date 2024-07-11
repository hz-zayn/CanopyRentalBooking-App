package com.example.canopymobile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.detail_image);
        descriptionTextView = findViewById(R.id.detail_description);

        // Get image resource and description from intent
        int imageResource = getIntent().getIntExtra("image_resource", R.drawable.aircond); // Default placeholder image
        String description = getIntent().getStringExtra("image_description");

        // Set image and description
        imageView.setImageResource(imageResource);
        descriptionTextView.setText(description);
    }
}