package com.securebank.service;

import com.securebank.model.Transaction;
import com.securebank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudScoringService {

    @Autowired
    private FraudGraph fraudGraph;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Calculates the fraud risk score for a given transaction.
     * Scores are based on amount, graph cycle detection, velocity, and out-degree.
     */
    public int calculateFraudScore(Transaction tx) {
        // 1. Add transaction edge to fraud graph
        fraudGraph.addTransactionEdge(tx);

        // 2. Score by transaction amount
        int amountScore = (tx.getAmount() > 100_000) ? 3
                : (tx.getAmount() > 50_000) ? 2
                : 1;

        // 3. Check for suspicious cycle in graph
        int cycleScore = fraudGraph.hasSuspiciousCycle(tx.getSender().getId()) ? 3 : 0;

        // 4. Calculate velocity score (time since last transaction from sender)
        int velocityScore = 1; // default low risk

        // Get latest transaction by this sender before current tx time
        List<Transaction> senderTxs = transactionRepository.findBySenderIdOrderByTimestampDesc(tx.getSender().getId());
        if (!senderTxs.isEmpty()) {
            Transaction lastTx = senderTxs.get(0);
            LocalDateTime lastTxTime = lastTx.getTimestamp();

            Duration gap = Duration.between(lastTxTime, tx.getTimestamp());
            long minutes = gap.toMinutes();

            if (minutes < 1) velocityScore = 3;
            else if (minutes < 5) velocityScore = 2;
            else velocityScore = 1;
        }

        // 5. Out-degree score (number of unique receivers)
        int outDegree = fraudGraph.getOutDegree(tx.getSender().getId());
        int outDegreeScore = (outDegree > 10) ? 3 : (outDegree > 5) ? 2 : 1;

        // 6. Sum all scores for total risk score
        int totalScore = amountScore + cycleScore + velocityScore + outDegreeScore;

        return totalScore;
    }
}
