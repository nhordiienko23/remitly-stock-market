package com.remitly.remitlystockmarket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bank_stocks")
@Getter
@Setter
public class BankStock {
    @Id
    private String stockName;
    private Long quantity;
}