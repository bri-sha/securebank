package com.securebank.repository;

import com.securebank.model.Transaction;
import com.securebank.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderId(Long senderId);
    List<Transaction> findByReceiverId(Long receiverId);
    List<Transaction> findBySenderIdOrderByTimestampDesc(Long senderId);
    
    // Count transactions by sender
    long countBySenderId(Long senderId);
    
    // Find high-risk transactions
    List<Transaction> findByFraudRiskScoreGreaterThan(int score);
    
    // Find transactions between two users
    @Query("SELECT t FROM Transaction t WHERE (t.sender.id = :userId1 AND t.receiver.id = :userId2) OR (t.sender.id = :userId2 AND t.receiver.id = :userId1)")
    List<Transaction> findTransactionsBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    // Get total amount sent by a user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.sender.id = :senderId")
    double getTotalAmountSentByUser(@Param("senderId") Long senderId);
    
    // Get total amount received by a user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.receiver.id = :receiverId")
    double getTotalAmountReceivedByUser(@Param("receiverId") Long receiverId);
}
