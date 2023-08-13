package org.inventorymanagement.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private String customerName;
    private String deliveryDetails;
    private String pastryName;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Pastry> orderItems;
    public Order(int orderId, String customerName, String deliveryDetails, String pastryName,int quantity, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.deliveryDetails = deliveryDetails;
        this.pastryName = pastryName;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = new ArrayList<>();
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getdeliveryDetails() {
        return deliveryDetails;
    }
    public String getPastryName() {
        return pastryName;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public boolean matches(String customerName, String deliveryDetails, String pastryName, int quantity) {
        return this.customerName.equals(customerName) &&
                this.deliveryDetails.equals(deliveryDetails) &&
                this.pastryName.equals(pastryName) &&
                this.quantity == quantity;
    }
}
