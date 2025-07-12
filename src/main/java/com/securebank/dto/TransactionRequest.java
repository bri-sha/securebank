package com.securebank.dto;

public class TransactionRequest {
    private Long senderId;
    private Long receiverId;
    private double amount;

    // Constructors
    public TransactionRequest() {}

    public TransactionRequest(Long senderId, Long receiverId, double amount) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Basic validation
    public boolean isValid() {
        return senderId != null && receiverId != null && amount > 0 && !senderId.equals(receiverId);
    }
}