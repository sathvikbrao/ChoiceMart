package com.example.choicemart1;

public class Order {
    private String orderNumber;
    private String grandTotalPrice;
    private String productName;
    private String shipmentAddress;
    private String orderDate; // New field: orderDate

    public Order() {
        // Default constructor required for Firebase database
    }

    public Order(String orderNumber, String grandTotalPrice, String productName, String shipmentAddress, String orderDate) {
        this.orderNumber = orderNumber;
        this.grandTotalPrice = grandTotalPrice;
        this.productName = productName;
        this.shipmentAddress = shipmentAddress;
        this.orderDate = orderDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getGrandTotalPrice() {
        return grandTotalPrice;
    }

    public void setGrandTotalPrice(String grandTotalPrice) {
        this.grandTotalPrice = grandTotalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShipmentAddress() {
        return shipmentAddress;
    }

    public void setShipmentAddress(String shipmentAddress) {
        this.shipmentAddress = shipmentAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
