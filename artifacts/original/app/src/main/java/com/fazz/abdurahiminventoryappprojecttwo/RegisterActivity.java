package com.fazz.abdurahiminventoryappprojecttwo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.editTextRegisterUsername);
        passwordEditText = findViewById(R.id.editTextRegisterPassword);
        Button registerButton = findViewById(R.id.buttonRegisterAccount);
        Button backButton = findViewById(R.id.buttonBackToLogin);

        registerButton.setOnClickListener(view -> registerUser());
        backButton.setOnClickListener(view -> finish());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // The project stores plain text passwords to keep the student database example simple.
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.userExists(username)) {
            Toast.makeText(this, "That username already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean created = databaseHelper.addUser(username, password);
        if (created) {
            Toast.makeText(this, "Account created. Please log in.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Account could not be created.", Toast.LENGTH_SHORT).show();
        }
    }
}
