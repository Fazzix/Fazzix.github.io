package com.fazz.abdurahiminventoryappprojecttwo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SmsPermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    private TextView permissionStatusTextView;
    private TextView notificationPreviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_permission);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.smsMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        permissionStatusTextView = findViewById(R.id.textPermissionStatus);
        notificationPreviewTextView = findViewById(R.id.textNotificationPreview);
        Button requestPermissionButton = findViewById(R.id.buttonRequestSmsPermission);
        Button continueButton = findViewById(R.id.buttonContinueInventory);

        // Check the current SMS permission state whenever the screen opens.
        updatePermissionMessage();

        requestPermissionButton.setOnClickListener(view -> requestSmsPermission());
        continueButton.setOnClickListener(view -> finish());
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
            updatePermissionMessage();
            return;
        }

        // This triggers the system dialog where the user can allow or deny SMS access.
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.SEND_SMS},
                SMS_PERMISSION_REQUEST_CODE
        );
    }

    private void updatePermissionMessage() {
        boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;

        if (hasPermission) {
            permissionStatusTextView.setText(R.string.sms_permission_granted);
            notificationPreviewTextView.setText(R.string.sms_low_inventory_enabled);
        } else {
            permissionStatusTextView.setText(R.string.sms_permission_denied);
            notificationPreviewTextView.setText(R.string.sms_low_inventory_disabled);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. App will continue without SMS alerts.", Toast.LENGTH_LONG).show();
            }
            updatePermissionMessage();
        }
    }
}
