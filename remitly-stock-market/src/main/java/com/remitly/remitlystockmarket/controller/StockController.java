package com.remitly.remitlystockmarket.controller;

import com.remitly.remitlystockmarket.model.BankStock;
import com.remitly.remitlystockmarket.repository.AuditLogRepository;
import com.remitly.remitlystockmarket.repository.BankStockRepository;
import com.remitly.remitlystockmarket.repository.WalletRepository;
import com.remitly.remitlystockmarket.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class StockController {

    private final StockService stockService;
    private final BankStockRepository bankRepo;
    private final WalletRepository walletRepo;
    private final AuditLogRepository auditLogRepo;

    public StockController(StockService stockService,
                           BankStockRepository bankRepo,
                           WalletRepository walletRepo,
                           AuditLogRepository auditLogRepo) {
        this.stockService = stockService;
        this.bankRepo = bankRepo;
        this.walletRepo = walletRepo;
        this.auditLogRepo = auditLogRepo;
    }

    // DTO class to parse the JSON request
    public static class StockRequest {
        public List<BankStock> stocks;
    }

    //set stocks
    @PostMapping("/stocks")
    public ResponseEntity<String> setBankStocks(@RequestBody StockRequest request) {
        if (request.stocks == null) {
            return ResponseEntity.badRequest().body("Stocks list cannot be empty");
        }
        stockService.setBankStocks(request.stocks);
        return ResponseEntity.ok("Bank stocks updated successfully");
    }

    // GET /stocks
    @GetMapping("/stocks")
    public ResponseEntity<Map<String, Object>> getBankStocks() {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("stocks", bankRepo.findAll());
        return ResponseEntity.ok(response);
    }

    //buy or sell stock
    // POST /wallets/{wallet_id}/stocks/{stock_name}
    @PostMapping("/wallets/{walletId}/stocks/{stockName}")
    public ResponseEntity<String> handleStockOperation(
            @PathVariable String walletId,
            @PathVariable String stockName,
            @RequestBody Map<String, String> body) {

        String type = body.get("type");

        try {
            if ("buy".equalsIgnoreCase(type)) {
                stockService.buyStock(walletId, stockName);
            } else if ("sell".equalsIgnoreCase(type)) {
                stockService.sellStock(walletId, stockName);
            } else {
                return ResponseEntity.badRequest().body("Invalid type. Use 'buy' or 'sell'");
            }
            return ResponseEntity.ok("Operation successful");
        } catch (RuntimeException e) {
            // Returns 400 for business logic errors (not enough stocks/wallet missing)
            // Or 404 if stock not found
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //check wallet by ID
    // GET /wallets/{wallet_id}
    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<?> getWallet(@PathVariable String walletId) {
        return walletRepo.findById(walletId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /wallets/{wallet_id}/stocks/{stock_name}
    @GetMapping("/wallets/{walletId}/stocks/{stockName}")
    public ResponseEntity<Long> getStockQuantity(@PathVariable String walletId, @PathVariable String stockName) {
        return walletRepo.findById(walletId)
                .map(w -> ResponseEntity.ok(w.getStocks().getOrDefault(stockName, 0L)))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /log
    @GetMapping("/log")
    public ResponseEntity<Map<String, Object>> getLogs() {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("log", auditLogRepo.findAllByOrderByTimestampAsc());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chaos")
    public void chaos() {
        System.exit(1); // Forcefully kills the JVM instance
    }
}