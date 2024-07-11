package com.example.canopymobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, emailEditText, numCanopiesEditText, dateEditText;
    private TextView totalPriceTextView;
    private Spinner paymentMethodSpinner;
    private Button bookNowButton;

    private String canopyType;
    private int pricePerCanopy;

    private String[] paymentMethods = {"Credit Card", "Debit Card", "Online Banking"};
    private String selectedPaymentMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize EditTexts, Spinner, and Button
        nameEditText = findViewById(R.id.name_edittext);
        phoneEditText = findViewById(R.id.phone_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        numCanopiesEditText = findViewById(R.id.num_canopies_edittext);
        dateEditText = findViewById(R.id.date_edittext);
        paymentMethodSpinner = findViewById(R.id.payment_method_spinner);
        totalPriceTextView = findViewById(R.id.total_price_textview);
        bookNowButton = findViewById(R.id.book_now_button);

        // Get data from the Intent
        canopyType = getIntent().getStringExtra("CANOPY_TYPE");
        pricePerCanopy = Integer.parseInt(getIntent().getStringExtra("PRICE").replace("RM", "").trim());

        // Set up the Book Now button click listener
        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotalPrice(); // Ensure total price is calculated before booking
                bookNow();
            }
        });

        // Calculate and display the total price when the number of canopies changes
        numCanopiesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalPrice();
            }
        });

        // Initialize EditText fields with user information from FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Set the retrieved values to EditText fields
            if (name != null && !name.isEmpty()) {
                nameEditText.setText(name);
            }
            if (email != null && !email.isEmpty()) {
                emailEditText.setText(email);
            }
        }

        // Set up the DatePickerDialog
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethod = paymentMethods[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Note: month is 0-based, so we need to add 1 to it.
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateEditText.setText(date);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void calculateTotalPrice() {
        String numCanopiesText = numCanopiesEditText.getText().toString().trim();
        if (!numCanopiesText.isEmpty()) {
            int numCanopies = Integer.parseInt(numCanopiesText);
            int totalPrice = numCanopies * pricePerCanopy;
            totalPriceTextView.setText("Total Price: RM" + totalPrice);
        }
    }

    private void bookNow() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String numCanopies = numCanopiesEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String totalPrice = totalPriceTextView.getText().toString().replace("Total Price: RM", "").trim();

        // Validate input fields
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || numCanopies.isEmpty() || date.isEmpty() || selectedPaymentMethod.isEmpty() || totalPrice.isEmpty()) {
            Toast.makeText(this, "Please complete all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store the booking details
        Map<String, String> bookingDetails = new HashMap<>();
        bookingDetails.put("name", name);
        bookingDetails.put("phone", phone);
        bookingDetails.put("email", email);
        bookingDetails.put("canopyType", canopyType);
        bookingDetails.put("pricePerCanopy", String.valueOf(pricePerCanopy));
        bookingDetails.put("numCanopies", numCanopies);
        bookingDetails.put("date", date);
        bookingDetails.put("paymentMethod", selectedPaymentMethod);
        bookingDetails.put("totalPrice", totalPrice);

        // Get the current user's UID and store booking details under their UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings").child(userId);
            String bookingId = bookingsRef.push().getKey();
            if (bookingId != null) {
                bookingsRef.child(bookingId).setValue(bookingDetails);
            }
        }

        // Send a push notification
        sendNotification();

        // Show a confirmation message or handle the next step
        Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show();

        // Start BookingConfirmationActivity and pass the booking details
        Intent intent = new Intent(this, BookingConfirmationActivity.class);
        startActivity(intent);
    }

    private void sendNotification() {
        // Build the notification message
        String title = "Booking Confirmed";
        String message = "Your booking has been confirmed.";

        // Subscribe to the topic for notifications (if not already subscribed)
        FirebaseMessaging.getInstance().subscribeToTopic("bookings");

        // Create notification data
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("message", message);

        // Send notification to Firebase Cloud Messaging
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notificationsRef = database.getReference("notifications");
        String notificationId = notificationsRef.push().getKey();
        if (notificationId != null) {
            notificationsRef.child(notificationId).setValue(notificationData);
        }
    }
}