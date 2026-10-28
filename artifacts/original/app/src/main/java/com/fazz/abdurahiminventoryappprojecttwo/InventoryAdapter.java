package com.fazz.abdurahiminventoryappprojecttwo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    public interface InventoryClickListener {
        void onItemSelected(InventoryItem item);

        void onDeleteClicked(InventoryItem item);
    }

    private final InventoryClickListener clickListener;
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    public InventoryAdapter(InventoryClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);
        holder.itemNameTextView.setText(item.getItemName());
        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
        holder.itemView.setOnClickListener(view -> clickListener.onItemSelected(item));
        holder.editButton.setOnClickListener(view -> clickListener.onItemSelected(item));
        holder.deleteButton.setOnClickListener(view -> clickListener.onDeleteClicked(item));
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    public void setInventoryItems(List<InventoryItem> items) {
        inventoryItems = items;
        notifyDataSetChanged();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView;
        TextView quantityTextView;
        Button editButton;
        Button deleteButton;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.textItemName);
            quantityTextView = itemView.findViewById(R.id.textItemQuantity);
            editButton = itemView.findViewById(R.id.buttonSelectItem);
            deleteButton = itemView.findViewById(R.id.buttonDeleteItem);
        }
    }
}
