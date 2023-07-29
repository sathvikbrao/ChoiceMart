package com.example.choicemart1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choicemart1.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrdersAdapter.CancelOrderListener {

    private RecyclerView ordersRecyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;

    private DatabaseReference ordersRef;
    private FirebaseUser currentUser;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase references
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(orderList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ordersRecyclerView.setAdapter(ordersAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchOrders();
    }

    private void fetchOrders() {
        String phone = UserData.getInstance().getUserId();
        System.out.println(phone);
        Query query = ordersRef.child(phone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("inside ordersnapsot");
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        order.setOrderNumber(orderSnapshot.getKey());
                        order.setOrderDate(orderSnapshot.child("orderDate").getValue(String.class));
                        System.out.println("fetching order");
                        orderList.add(order);
                    }
                }
                ordersAdapter.notifyDataSetChanged();
                if (orderList.isEmpty()) {
                    // Log a message indicating no orders are available
                    Log.d("OrdersFragment", "Authentication problem");
                } else {
                    // Log a message indicating the number of orders available
                    Log.d("OrdersFragment", "Number of orders: " + orderList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCancelOrder(String orderNumber) {
        // Perform cancel order action based on the order number
        String phone = UserData.getInstance().getUserId();
        System.out.println(orderNumber);
        DatabaseReference orderRef = ordersRef.child(phone).child(orderNumber);

        // Show a confirmation dialog before canceling the order
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Order");
        builder.setMessage("Are you sure you want to cancel this order?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User clicked "Yes," cancel the order
            orderRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Order canceled successfully
                        showToast("Order canceled successfully");
                        // Remove the canceled order from the list and update the RecyclerView
                        for (Order order : orderList) {
                            if (order.getOrderNumber().equals(orderNumber)) {
                                orderList.remove(order);
                                break;
                            }
                        }
                        ordersAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to cancel the order
                        showToast("Failed to cancel the order");
                    });
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked "No," do nothing
            dialog.dismiss();
        });
        builder.create().show();
    }


    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

