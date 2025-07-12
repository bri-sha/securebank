package com.securebank.controller;

import com.securebank.model.Transaction;
import com.securebank.dto.TransactionRequest;
import com.securebank.dto.TransactionResponse;
import com.securebank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // ✅ Create a new transaction
    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
        try {
            // Validate request
            if (!request.isValid()) {
                return ResponseEntity.badRequest().body("Invalid transaction data. Please check sender, receiver, and amount.");
            }

            Transaction tx = transactionService.createTransaction(
                request.getSenderId(), 
                request.getReceiverId(), 
                request.getAmount()
            );

            // Convert to response DTO
            TransactionResponse response = convertToResponse(tx);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Transaction failed: " + e.getMessage());
        }
    }

    // ✅ Get transactions by sender user ID
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<?> getBySender(@PathVariable Long senderId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsBySender(senderId);
            List<TransactionResponse> responses = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving transactions: " + e.getMessage());
        }
    }

    // ✅ Get transactions by receiver user ID
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<?> getByReceiver(@PathVariable Long receiverId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByReceiver(receiverId);
            List<TransactionResponse> responses = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving transactions: " + e.getMessage());
        }
    }

    // ✅ Get all transactions (for admin purposes)
    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            List<TransactionResponse> responses = transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving transactions: " + e.getMessage());
        }
    }

    // ✅ Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            TransactionResponse response = convertToResponse(transaction);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Transaction not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving transaction: " + e.getMessage());
        }
    }

    // Helper method to convert Transaction to TransactionResponse
    private TransactionResponse convertToResponse(Transaction tx) {
        String status = tx.getFraudRiskScore() > 7 ? "HIGH_RISK" : 
                       tx.getFraudRiskScore() > 4 ? "MEDIUM_RISK" : "LOW_RISK";
        
        return new TransactionResponse(
            tx.getId(),
            tx.getSender().getId(),
            tx.getSender().getEmail(),
            tx.getReceiver().getId(),
            tx.getReceiver().getEmail(),
            tx.getAmount(),
            tx.getTimestamp(),
            tx.getFraudRiskScore(),
            status
        );
    }
}
