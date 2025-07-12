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
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new Exception("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new Exception("Receiver not found"));

        Transaction transaction = new Transaction(sender, receiver, amount);

        int fraudScore = fraudScoringService.calculateFraudScore(transaction);
        transaction.setFraudRiskScore(fraudScore);

        return transactionRepository.save(transaction);
    }

    // ✅ Get all transactions by sender ID
    public List<Transaction> getTransactionsBySender(Long senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

    // ✅ Get all transactions by receiver ID
    public List<Transaction> getTransactionsByReceiver(Long receiverId) {
        return transactionRepository.findByReceiverId(receiverId);
    }
}
