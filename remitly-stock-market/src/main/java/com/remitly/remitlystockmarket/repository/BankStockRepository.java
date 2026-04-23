package com.remitly.remitlystockmarket.repository;

import com.remitly.remitlystockmarket.model.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStockRepository extends JpaRepository<BankStock, String> {
    // JpaRepository provides methods like findById, save, etc. automatically
}