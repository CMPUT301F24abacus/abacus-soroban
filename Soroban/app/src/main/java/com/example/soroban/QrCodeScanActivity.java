/**
 *  Author: Ayan Chaudhry
 *  References: ChatGPT and StackOverflow
 */

package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.EventRegistrationActivity;

/**
 * Activity to scan QR code and redirect to EventRegistrationActivity
 */
public class QrCodeScanActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        // Find the QR code image
        ImageView qrCodeImage = findViewById(R.id.qr_code_icon);

        // Set click listener to redirect to EventRegistrationActivity
        qrCodeImage.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the QR code image is clicked.
             * @param v
             */
            @Override
            public void onClick(View v) {
                // Navigate to EventRegistrationActivity
                Intent intent = new Intent(QrCodeScanActivity.this, EventRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}