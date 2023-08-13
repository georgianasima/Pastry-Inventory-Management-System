package org.inventorymanagement.entities;

import java.util.List;

public class Pastry {
    private String name;
    private int quantity;
    private double price;
    private List<Ingredient> ingredients;

    public Pastry(String name, int quantity, double price, List<Ingredient> ingredients) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.ingredients = ingredients;
    }
    public Pastry(String name, int quantity, double price) {
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
    public double getPrice() {
        return price;
    }


}
