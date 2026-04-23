package com.remitly.remitlystockmarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated ID for log entry

    private String type;        // 'buy' or 'sell'
    private String walletId;
    private String stockName;
    private LocalDateTime timestamp; // When the operation occurred

    // It's good practice to set timestamp automatically
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}