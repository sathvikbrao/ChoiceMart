package com.example.choicemart1.models;

import com.google.gson.annotations.SerializedName;



import java.io.Serializable;


public class Product implements Serializable {

    private String grandTotal;

    public Product() {
        // Empty constructor required for Firebase
    }
    private String name;




    public String getName() {
        return name;
    }
    private int quantity;
    private String category;
    private String priceString; // Rename the variable to priceString

    private String ProductName;
    private String ProductPrice;
    private String ProductDescription;
    private String ProductCategory;
    private String ProductImage;

    public Product(String productName, String productPrice, String productDescription, String productCategory, String productImage) {
        this.ProductName = productName;
        this.ProductPrice = productPrice;
        this.ProductDescription = productDescription;
        this.ProductCategory = productCategory;
        this.ProductImage = productImage;
    }
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }


    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    @SerializedName("price")
    private double price; // Rename the variable to priceValue

    @SerializedName("description")
    private String description;

    public Product(String title, String priceString, String image) {
        this.title = title;
        this.priceString = priceString;
        System.out.println("Extra hello "+priceString);
        this.image = image;
    }

    // Getters and setters for the properties

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceString() {
        return priceString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Additional functions


    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public String getProductImageUrl() {
        return image;
    }

    public String getProductName() {
        return title;
    }

    public double getProductPriceInRupees() {
        double conversionRate = 75.0;
        return price * conversionRate;
    }

    public void setProductCategory(String category) {
        this.category = category;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal=grandTotal;
    }
    public String getGrandTotal() {
        return grandTotal;
    }
}
