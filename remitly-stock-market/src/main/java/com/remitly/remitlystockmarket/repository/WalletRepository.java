package com.remitly.remitlystockmarket.repository;

import com.remitly.remitlystockmarket.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    // Handles database operations for Wallet entities
}