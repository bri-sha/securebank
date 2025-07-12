package com.securebank.service;

import com.securebank.model.Transaction;
import com.securebank.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FraudGraph {

    // Graph represented as adjacency list: sender -> set of receivers
    private final Map<Long, Set<Long>> graph = new HashMap<>();

    // Add an edge for each transaction (sender -> receiver)
    public void addTransactionEdge(Transaction tx) {
        Long senderId = tx.getSender().getId();
        Long receiverId = tx.getReceiver().getId();

        graph.computeIfAbsent(senderId, k -> new HashSet<>()).add(receiverId);
    }

    // Detect cycles starting from a given user using DFS
    public boolean hasSuspiciousCycle(Long userId) {
        Set<Long> visited = new HashSet<>();
        Set<Long> recursionStack = new HashSet<>();
        return dfsCycleCheck(userId, visited, recursionStack);
    }

    private boolean dfsCycleCheck(Long current, Set<Long> visited, Set<Long> stack) {
        if (stack.contains(current)) {
            // Cycle detected
            return true;
        }
        if (visited.contains(current)) {
            return false;
        }

        visited.add(current);
        stack.add(current);

        Set<Long> neighbors = graph.getOrDefault(current, Collections.emptySet());
        for (Long neighbor : neighbors) {
            if (dfsCycleCheck(neighbor, visited, stack)) {
                return true;
            }
        }
        stack.remove(current);
        return false;
    }

    // Get out-degree (number of distinct receivers from this sender)
    public int getOutDegree(Long userId) {
        return graph.getOrDefault(userId, Collections.emptySet()).size();
    }
}
