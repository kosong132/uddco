package com.uddco.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Wishlist {

    private String wishlistId; // Auto-generated or UUID
    private String productId;
    private String productName;
    private String selectedColor;
    private String selectedSize;
    private String selectedCustomization;
    private double pricePerUnit;
    private double totalPrice;
    private int quantity;
    private String logoPosition;
    private String imageUrl;
    private String userId;
    private String createdAt; // ISO timestamp
    private String status;    // e.g., "active", "saved", etc.

    // No-arg constructor (needed for Firestore deserialization)
    public Wishlist() {
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.status = "active"; // default status
    }

    // Full constructor
    public Wishlist(String wishlistId, String productId, String productName, String selectedColor,
                    String selectedSize, String selectedCustomization, double pricePerUnit,
                    double totalPrice, int quantity, String logoPosition, String imageUrl,
                    String userId) {
        this.wishlistId = wishlistId;
        this.productId = productId;
        this.productName = productName;
        this.selectedColor = selectedColor;
        this.selectedSize = selectedSize;
        this.selectedCustomization = selectedCustomization;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.logoPosition = logoPosition;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.status = "active";
    }

    // Getters and Setters
    public String getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(String wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    public String getSelectedCustomization() {
        return selectedCustomization;
    }

    public void setSelectedCustomization(String selectedCustomization) {
        this.selectedCustomization = selectedCustomization;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLogoPosition() {
        return logoPosition;
    }

    public void setLogoPosition(String logoPosition) {
        this.logoPosition = logoPosition;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
