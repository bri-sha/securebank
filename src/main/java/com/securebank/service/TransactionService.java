package com.securebank.service;

import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.repository.TransactionRepository;
import com.securebank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FraudScoringService fraudScoringService;

    // ✅ Create a transaction with fraud scoring
    public Transaction createTransaction(Long senderId, Long receiverId, double amount) throws Exception {
        // Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        // Validate that sender and receiver are different
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with ID: " + senderId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found with ID: " + receiverId));

        Transaction transaction = new Transaction(sender, receiver, amount);

        // Calculate fraud score
        int fraudScore = fraudScoringService.calculateFraudScore(transaction);
        transaction.setFraudRiskScore(fraudScore);

        // Save and return the transaction
        return transactionRepository.save(transaction);
    }

    // ✅ Get all transactions by sender ID
    public List<Transaction> getTransactionsBySender(Long senderId) {
        if (!userRepository.existsById(senderId)) {
            throw new IllegalArgumentException("Sender not found with ID: " + senderId);
        }
        return transactionRepository.findBySenderId(senderId);
    }

    // ✅ Get all transactions by receiver ID
    public List<Transaction> getTransactionsByReceiver(Long receiverId) {
        if (!userRepository.existsById(receiverId)) {
            throw new IllegalArgumentException("Receiver not found with ID: " + receiverId);
        }
        return transactionRepository.findByReceiverId(receiverId);
    }

    // ✅ Get all transactions (for admin purposes)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // ✅ Get transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + id));
    }

    // ✅ Get transaction count by sender (for analytics)
    public long getTransactionCountBySender(Long senderId) {
        return transactionRepository.countBySenderId(senderId);
    }

    // ✅ Get high-risk transactions (fraud score > 7)
    public List<Transaction> getHighRiskTransactions() {
        return transactionRepository.findByFraudRiskScoreGreaterThan(7);
    }
}
