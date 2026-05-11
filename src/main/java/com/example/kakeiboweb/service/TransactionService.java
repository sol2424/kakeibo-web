package com.example.kakeiboweb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.kakeiboweb.entity.Transaction;
import com.example.kakeiboweb.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }
    
    public List<Transaction> findAll() {
        return repository.findAll();
    }

    public void save(Transaction t) {
        repository.save(t);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    //↓ここから合計処理
    
    public Transaction findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
    
    public int getTotalIncome() {
        return repository.findAll().stream()
                .filter(t -> "income".equals(t.getType()))
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    public int getTotalExpense() {
        return repository.findAll().stream()
                .filter(t -> "expense".equals(t.getType()))
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    public int getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
    
    //合計表示 
    
    public int getIncomeByMonth(int year, int month) {
        return repository.findAll().stream()
            .filter(t -> t.getType().equals("income"))
            .filter(t -> t.getDate().getYear() == year)
            .filter(t -> t.getDate().getMonthValue() == month)
            .mapToInt(Transaction::getAmount)
            .sum();
    }

    public int getExpenseByMonth(int year, int month) {
        return repository.findAll().stream()
            .filter(t -> t.getType().equals("expense"))
            .filter(t -> t.getDate().getYear() == year)
            .filter(t -> t.getDate().getMonthValue() == month)
            .mapToInt(Transaction::getAmount)
            .sum();
    }
    
    //月別グラフ
    
    public Map<Integer, Integer> getMonthlyExpense() {

        return repository.findAll().stream()
                .filter(t -> "expense".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonthValue(), // 月
                        Collectors.summingInt(Transaction::getAmount)
                ));
    }
    
    //カテゴリ別グラフ
    
    public Map<String, Integer> getCategoryExpense() {
        return repository.findAll().stream()
                .filter(t -> "expense".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingInt(Transaction::getAmount)
                ));
    }
    
    //フィルタ処理
    
    public List<Transaction> filter(int year, int month, boolean showIncome, boolean showExpense) {

        return repository.findAll().stream()
            .filter(t -> t.getDate().getYear() == year)
            .filter(t -> t.getDate().getMonthValue() == month)
            .filter(t -> 
                (showIncome && t.getType().equals("income")) ||
                (showExpense && t.getType().equals("expense"))
            )
            .toList();
    }
}