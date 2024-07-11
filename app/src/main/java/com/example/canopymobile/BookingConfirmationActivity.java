package com.example.canopymobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView bookingDetailsTextView;
    private Button homeButton;
    private FirebaseAuth mAuth;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        bookingDetailsTextView = findViewById(R.id.booking_details_textview);
        homeButton = findViewById(R.id.home_button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings").child(userId);

            // Retrieve booking details from Firebase
            bookingsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        StringBuilder bookingDetails = new StringBuilder();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, String> bookingData = (Map<String, String>) snapshot.getValue();
                            if (bookingData != null) {
                                bookingDetails.append("Name: ").append(bookingData.get("name")).append("\n")
                                        .append("Phone: ").append(bookingData.get("phone")).append("\n")
                                        .append("Email: ").append(bookingData.get("email")).append("\n")
                                        .append("Canopy Type: ").append(bookingData.get("canopyType")).append("\n")
                                        .append("Price Per Canopy: RM").append(bookingData.get("pricePerCanopy")).append("\n")
                                        .append("Number of Canopies: ").append(bookingData.get("numCanopies")).append("\n")
                                        .append("Date: ").append(bookingData.get("date")).append("\n")
                                        .append("Payment Method: ").append(bookingData.get("paymentMethod")).append("\n")
                                        .append("Total Price: RM").append(bookingData.get("totalPrice")).append("\n\n");
                            }
                        }
                        bookingDetailsTextView.setText(bookingDetails.toString());
                    } else {
                        bookingDetailsTextView.setText("No bookings found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    bookingDetailsTextView.setText("Failed to load bookings.");
                }
            });
        }

        // Set click listener for Home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    // Method to go back to MainActivity
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // finish this activity to prevent user from coming back to it on back press
    }
}