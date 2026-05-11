package com.example.kakeiboweb.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;
    private int amount;
    private String category;
    private String memo;
    private String type;

    // --- getter / setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}