package com.example.kakeiboweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kakeiboweb.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}