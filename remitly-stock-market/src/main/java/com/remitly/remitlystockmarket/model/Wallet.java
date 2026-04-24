package com.remitly.remitlystockmarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    private String walletId; // Unique identifier for the wallet

    // We use ElementCollection to store stock names and quantities directly in the database
    @ElementCollection
    @CollectionTable(name = "wallet_stocks", joinColumns = @JoinColumn(name = "wallet_id"))
    @MapKeyColumn(name = "stock_name")
    @Column(name = "quantity")
    private Map<String, Long> stocks = new HashMap<>();


    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public Map<String, Long> getStocks() {
        return stocks;
    }

    public void setStocks(Map<String, Long> stocks) {
        this.stocks = stocks;
    }
}