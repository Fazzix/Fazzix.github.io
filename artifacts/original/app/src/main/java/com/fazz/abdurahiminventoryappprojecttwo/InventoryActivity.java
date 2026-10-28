package com.fazz.abdurahiminventoryappprojecttwo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText quantityEditText;
    private DatabaseHelper databaseHelper;
    private InventoryAdapter inventoryAdapter;
    private int selectedItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inventoryMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        itemNameEditText = findViewById(R.id.editTextItemName);
        quantityEditText = findViewById(R.id.editTextQuantity);
        Button addItemButton = findViewById(R.id.buttonAddItem);
        Button updateItemButton = findViewById(R.id.buttonUpdateItem);
        Button clearSelectionButton = findViewById(R.id.buttonClearSelection);
        Button smsSettingsButton = findViewById(R.id.buttonSmsSettings);
        RecyclerView inventoryRecyclerView = findViewById(R.id.recyclerViewInventory);

        inventoryAdapter = new InventoryAdapter(new InventoryAdapter.InventoryClickListener() {
            @Override
            public void onItemSelected(InventoryItem item) {
                selectInventoryItem(item);
            }

            @Override
            public void onDeleteClicked(InventoryItem item) {
                deleteInventoryItem(item);
            }
        });

        inventoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        addItemButton.setOnClickListener(view -> addInventoryItem());
        updateItemButton.setOnClickListener(view -> updateInventoryItem());
        clearSelectionButton.setOnClickListener(view -> clearInputs());

        // Open the SMS permission screen. The permission request happens from that screen.
        smsSettingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(InventoryActivity.this, SmsPermissionActivity.class);
            startActivity(intent);
        });

        loadInventoryItems();
    }

    private void addInventoryItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        Integer quantity = readQuantity();

        if (itemName.isEmpty() || quantity == null) {
            Toast.makeText(this, "Enter an item name and quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.addInventoryItem(itemName, quantity)) {
            Toast.makeText(this, "Item added.", Toast.LENGTH_SHORT).show();
            checkLowInventory(itemName, quantity);
            clearInputs();
            loadInventoryItems();
        } else {
            Toast.makeText(this, "Item could not be added.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInventoryItem() {
        if (selectedItemId == -1) {
            Toast.makeText(this, "Select an item to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemName = itemNameEditText.getText().toString().trim();
        Integer quantity = readQuantity();

        if (itemName.isEmpty() || quantity == null) {
            Toast.makeText(this, "Enter an item name and quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.updateInventoryItem(selectedItemId, itemName, quantity)) {
            Toast.makeText(this, "Item updated.", Toast.LENGTH_SHORT).show();
            checkLowInventory(itemName, quantity);
            clearInputs();
            loadInventoryItems();
        } else {
            Toast.makeText(this, "Item could not be updated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteInventoryItem(InventoryItem item) {
        if (databaseHelper.deleteInventoryItem(item.getId())) {
            Toast.makeText(this, "Item deleted.", Toast.LENGTH_SHORT).show();
            if (selectedItemId == item.getId()) {
                clearInputs();
            }
            loadInventoryItems();
        } else {
            Toast.makeText(this, "Item could not be deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectInventoryItem(InventoryItem item) {
        selectedItemId = item.getId();
        itemNameEditText.setText(item.getItemName());
        quantityEditText.setText(String.valueOf(item.getQuantity()));
        Toast.makeText(this, "Selected " + item.getItemName() + " for editing.", Toast.LENGTH_SHORT).show();
    }

    private void loadInventoryItems() {
        List<InventoryItem> items = databaseHelper.getAllInventoryItems();
        inventoryAdapter.setInventoryItems(items);
    }

    private Integer readQuantity() {
        String quantityText = quantityEditText.getText().toString().trim();
        if (quantityText.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(quantityText);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void clearInputs() {
        selectedItemId = -1;
        itemNameEditText.setText("");
        quantityEditText.setText("");
        itemNameEditText.clearFocus();
        quantityEditText.clearFocus();
    }

    private void checkLowInventory(String itemName, int quantity) {
        if (quantity == 0) {
            sendLowInventorySms(itemName);
        }
    }

    private void sendLowInventorySms(String itemName) {
        String message = "Low inventory alert: " + itemName + " is out of stock.";

        // Always show the low inventory alert so emulator testing can verify the trigger.
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            // In the emulator, 5554 is commonly used as a safe test destination.
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5554", null, message, null, null);
        } catch (Exception exception) {
            // Some emulators cannot send SMS, but the alert Toast above still confirms the trigger.
        }
    }
}
