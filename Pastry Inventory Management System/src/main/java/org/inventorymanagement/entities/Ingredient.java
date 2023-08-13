package org.inventorymanagement.entities;
public class Ingredient {
    private String name;
    private int quantity;
    private String expirationDate;
    public Ingredient(String name, int quantity, String expirationDate) {
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}
