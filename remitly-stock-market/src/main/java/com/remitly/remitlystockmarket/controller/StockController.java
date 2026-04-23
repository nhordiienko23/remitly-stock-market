package com.remitly.remitlystockmarket.controller;

import com.remitly.remitlystockmarket.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
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