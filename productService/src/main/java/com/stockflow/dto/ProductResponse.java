package com.stockflow.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String category;
    private int quantity;
    private double pricePerUnit;
    private double totalPrice;

    public ProductResponse() {}

    public ProductResponse(Long id, String name, String category, int quantity, double pricePerUnit, double totalPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}