package com.remitly.remitlystockmarket.service;

import com.remitly.remitlystockmarket.model.AuditLog;
import com.remitly.remitlystockmarket.model.BankStock;
import com.remitly.remitlystockmarket.model.Wallet;
import com.remitly.remitlystockmarket.repository.AuditLogRepository;
import com.remitly.remitlystockmarket.repository.BankStockRepository;
import com.remitly.remitlystockmarket.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StockService {
    private final BankStockRepository bankRepo;
    private final WalletRepository walletRepo;
    private final AuditLogRepository logRepo;

    public StockService(BankStockRepository bankRepo, WalletRepository walletRepo, AuditLogRepository logRepo) {
        this.bankRepo = bankRepo;
        this.walletRepo = walletRepo;
        this.logRepo = logRepo;
    }

    @Transactional
    public void buyStock(String walletId, String stockName) {
        // 1. Fetch bank stock or throw exception if it doesn't exist
        BankStock stock = bankRepo.findById(stockName)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // 2. Check liquidity (must have at least one stock available)
        if (stock.getQuantity() <= 0) {
            throw new RuntimeException("Not enough stocks in bank");
        }

        // 3. Update bank inventory
        stock.setQuantity(stock.getQuantity() - 1);
        bankRepo.save(stock);

        // 4. Find or create the wallet
        Wallet wallet = walletRepo.findById(walletId)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setWalletId(walletId);
                    return walletRepo.save(newWallet);
                });

        // 5. Add stock to wallet
        wallet.getStocks().put(stockName, wallet.getStocks().getOrDefault(stockName, 0L) + 1);
        walletRepo.save(wallet);

        // 6. Log successful operation
        logAction("buy",walletId,stockName);
    }

    @Transactional
    public void sellStock(String walletId, String stockName) {
        // 1. Check if wallet exists
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet doesn't exist"));

        // 2. Check if the user actually has this stock
        Long currentQuantity = wallet.getStocks().getOrDefault(stockName, 0L);
        if (currentQuantity <= 0) {
            throw new RuntimeException("No stocks in wallet to sell");
        }

        // 3. Fetch bank stock
        BankStock stock = bankRepo.findById(stockName)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // 4. Update wallet: remove one stock
        wallet.getStocks().put(stockName, currentQuantity - 1);
        walletRepo.save(wallet);

        // 5. Update bank inventory: add one stock back to bank
        stock.setQuantity(stock.getQuantity() + 1);
        bankRepo.save(stock);

        // 6. Log successful operation
        logAction("sell",walletId,stockName);
    }
    private void logAction(String type, String walletId, String stockName){
        AuditLog log = new AuditLog();
        log.setType(type);
        log.setWalletId(walletId);
        log.setStockName(stockName);
        log.setTimestamp(LocalDateTime.now());
        logRepo.save(log);
    }
}