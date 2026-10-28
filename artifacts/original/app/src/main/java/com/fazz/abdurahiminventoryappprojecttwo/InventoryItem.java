package com.fazz.abdurahiminventoryappprojecttwo;

public class InventoryItem {

    private final int id;
    private final String itemName;
    private final int quantity;

    public InventoryItem(int id, String itemName, int quantity) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }
}
