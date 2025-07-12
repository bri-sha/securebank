package com.securebank.controller;

import com.securebank.model.Transaction;
import com.securebank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // ✅ Create a new transaction
    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestParam Long senderId,
                                               @RequestParam Long receiverId,
                                               @RequestParam double amount) {
        try {
            Transaction tx = transactionService.createTransaction(senderId, receiverId, amount);
            return ResponseEntity.ok(tx);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Get transactions by sender user ID
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Transaction>> getBySender(@PathVariable Long senderId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsBySender(senderId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Get transactions by receiver user ID
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Transaction>> getByReceiver(@PathVariable Long receiverId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByReceiver(receiverId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
