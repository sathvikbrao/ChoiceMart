package com.example.choicemart1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choicemart1.Order;
import com.example.choicemart1.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private CancelOrderListener cancelOrderListener;

    public OrdersAdapter(List<Order> orderList, CancelOrderListener cancelOrderListener) {
        this.orderList = orderList;
        this.cancelOrderListener = cancelOrderListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderNumberTextView.setText(order.getOrderNumber());
        holder.productNameTextView.setText(order.getProductName());
        holder.grandTotalPriceTextView.setText(order.getGrandTotalPrice());
        holder.shipmentAddressTextView.setText(order.getShipmentAddress());
        holder.orderDateTextView.setText(order.getOrderDate());

        holder.cancelOrderButton.setOnClickListener(v -> {
            String orderNumber = order.getOrderNumber();
            if (cancelOrderListener != null) {

                cancelOrderListener.onCancelOrder(orderNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumberTextView;
        TextView productNameTextView;
        TextView grandTotalPriceTextView;
        TextView shipmentAddressTextView;
        TextView orderDateTextView;
        Button cancelOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberTextView = itemView.findViewById(R.id.orderNumberTextView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            grandTotalPriceTextView = itemView.findViewById(R.id.grandTotalPriceTextView);
            shipmentAddressTextView = itemView.findViewById(R.id.shipmentAddressTextView);
            orderDateTextView=itemView.findViewById(R.id.orderDateTextView);
            cancelOrderButton = itemView.findViewById(R.id.cancelButton);
        }
    }

    public interface CancelOrderListener {
        void onCancelOrder(String orderNumber);
    }
}
