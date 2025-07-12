package com.securebank.repository;

import com.securebank.model.Transaction;
import com.securebank.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderId(Long senderId);
    List<Transaction> findByReceiverId(Long receiverId);
    List<Transaction> findBySenderIdOrderByTimestampDesc(Long senderId);


}
