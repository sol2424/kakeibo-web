package com.example.kakeiboweb.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "日付を入力してください")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Min(value = 1, message = "金額は1円以上を入力してください")
    private int amount;

    @NotBlank(message = "カテゴリを入力してください")
    private String category;

    @Size(max = 100, message = "メモは100文字以内で入力してください")
    private String memo;

    @NotBlank(message = "収入/支出を選択してください")
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