package com.example.shardingjdbctest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private long orderId;
    private String data;
}
