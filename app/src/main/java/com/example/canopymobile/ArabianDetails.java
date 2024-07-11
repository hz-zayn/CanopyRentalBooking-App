package com.example.canopymobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ArabianDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_arabian_details);

        // Find the TextViews
        TextView canopyTypeTextView = findViewById(R.id.aircond_textView);
        TextView priceTextView = findViewById(R.id.textView4);

        // Find the Button
        Button bookNowButton = findViewById(R.id.button);

        // Set an OnClickListener on the Button
        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start BookingActivity
                Intent intent = new Intent(ArabianDetails.this, BookingActivity.class);

                // Pass the canopy type and price to BookingActivity
                intent.putExtra("CANOPY_TYPE", canopyTypeTextView.getText().toString());
                intent.putExtra("PRICE", priceTextView.getText().toString());

                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}