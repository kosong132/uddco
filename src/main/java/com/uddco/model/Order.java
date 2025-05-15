package com.uddco.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {

    private Long orderId;

    private String productId;
    private String productName;
    private String selectedColor;
    private String selectedSize;
    private String selectedCustomization;
    private String logoPosition;
    private int quantity;
    private double pricePerUnit;
    private String imageUrl;
    private double totalPrice;
    private String userId; // Optional: to link order to user
    private String createdAt; // Store timestamp as a String
    private String orderStatus = "Preparing"; // Default status

    public Order() {
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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



    public String getLogoPosition() {
        return logoPosition;
    }

    public void setLogoPosition(String logoPosition) {
        this.logoPosition = logoPosition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSelectedCustomization() {
        return selectedCustomization;
    }

    public void setSelectedCustomization(String selectedCustomization) {
        this.selectedCustomization = selectedCustomization;
    }

}
