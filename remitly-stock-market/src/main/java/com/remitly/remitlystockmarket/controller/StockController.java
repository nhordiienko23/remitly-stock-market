package com.remitly.remitlystockmarket.controller;

import com.remitly.remitlystockmarket.model.BankStock;
import com.remitly.remitlystockmarket.repository.BankStockRepository;
import com.remitly.remitlystockmarket.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class StockController {

    private final StockService stockService;
    private final BankStockRepository bankRepo;

    public StockController(StockService stockService, BankStockRepository bankRepo) {
        this.stockService = stockService;
        this.bankRepo = bankRepo;

    }
    // DTO class to parse the JSON request
    public static class StockRequest {
        public List<BankStock> stocks;
    }

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
        // Мы возвращаем список всех акций банка
        // Пока просто заглушка, чтобы проверить, что 404 пропал
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("stocks", bankRepo.findAll());
        return ResponseEntity.ok(response);
    }
  
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
}