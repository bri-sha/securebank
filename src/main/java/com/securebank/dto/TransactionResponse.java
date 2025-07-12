package com.securebank.dto;

import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private Long senderId;
    private String senderEmail;
    private Long receiverId;
    private String receiverEmail;
    private double amount;
    private LocalDateTime timestamp;
    private int fraudRiskScore;
    private String status;

    // Constructors
    public TransactionResponse() {}

    public TransactionResponse(Long id, Long senderId, String senderEmail, Long receiverId, 
                             String receiverEmail, double amount, LocalDateTime timestamp, 
                             int fraudRiskScore, String status) {
        this.id = id;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.receiverId = receiverId;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.timestamp = timestamp;
        this.fraudRiskScore = fraudRiskScore;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getFraudRiskScore() {
        return fraudRiskScore;
    }

    public void setFraudRiskScore(int fraudRiskScore) {
        this.fraudRiskScore = fraudRiskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}