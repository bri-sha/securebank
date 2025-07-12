package com.securebank.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.dto.AuthRequest;
import com.securebank.dto.AuthResponse;
import com.securebank.dto.TransactionRequest;
import com.securebank.dto.TransactionResponse;
import com.securebank.model.User;
import com.securebank.repository.UserRepository;
import com.securebank.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SecureBankIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User testUser1;
    private User testUser2;
    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up database
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setEmail("test1@example.com");
        testUser1.setPassword("password123");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPassword("password123");

        // Register users and get JWT token
        registerUsersAndLogin();
    }

    private void registerUsersAndLogin() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser1)))
                .andExpect(status().isOk());

        // Register second user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser2)))
                .andExpect(status().isOk());

        // Login with first user
        AuthRequest loginRequest = new AuthRequest("test1@example.com", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
        jwtToken = authResponse.getToken();
    }

    @Test
    void testUserRegistration() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        // Verify user was created
        assertTrue(userRepository.existsByEmail("newuser@example.com"));
    }

    @Test
    void testUserLogin() throws Exception {
        AuthRequest loginRequest = new AuthRequest("test1@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testInvalidLogin() throws Exception {
        AuthRequest loginRequest = new AuthRequest("test1@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test1@example.com"))
                .andExpect(jsonPath("$.username").value("testuser1"));
    }

    @Test
    void testCreateTransaction() throws Exception {
        // Get user IDs
        User sender = userRepository.findByEmail("test1@example.com").orElseThrow();
        User receiver = userRepository.findByEmail("test2@example.com").orElseThrow();

        TransactionRequest transactionRequest = new TransactionRequest(
            sender.getId(), receiver.getId(), 1000.0
        );

        MvcResult result = mockMvc.perform(post("/api/transactions/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.senderEmail").value("test1@example.com"))
                .andExpect(jsonPath("$.receiverEmail").value("test2@example.com"))
                .andExpect(jsonPath("$.fraudRiskScore").exists())
                .andExpect(jsonPath("$.status").exists())
                .andReturn();

        // Verify transaction was created
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void testCreateTransactionWithInvalidData() throws Exception {
        TransactionRequest invalidRequest = new TransactionRequest(1L, 1L, 1000.0); // Same sender and receiver

        mockMvc.perform(post("/api/transactions/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTransactionsBySender() throws Exception {
        // Create a transaction first
        User sender = userRepository.findByEmail("test1@example.com").orElseThrow();
        User receiver = userRepository.findByEmail("test2@example.com").orElseThrow();

        TransactionRequest transactionRequest = new TransactionRequest(
            sender.getId(), receiver.getId(), 500.0
        );

        mockMvc.perform(post("/api/transactions/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk());

        // Get transactions by sender
        mockMvc.perform(get("/api/transactions/sender/" + sender.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].senderEmail").value("test1@example.com"))
                .andExpect(jsonPath("$[0].amount").value(500.0));
    }

    @Test
    void testGetTransactionsByReceiver() throws Exception {
        // Create a transaction first
        User sender = userRepository.findByEmail("test1@example.com").orElseThrow();
        User receiver = userRepository.findByEmail("test2@example.com").orElseThrow();

        TransactionRequest transactionRequest = new TransactionRequest(
            sender.getId(), receiver.getId(), 750.0
        );

        mockMvc.perform(post("/api/transactions/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk());

        // Get transactions by receiver
        mockMvc.perform(get("/api/transactions/receiver/" + receiver.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].receiverEmail").value("test2@example.com"))
                .andExpect(jsonPath("$[0].amount").value(750.0));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/transactions/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFraudDetection() throws Exception {
        User sender = userRepository.findByEmail("test1@example.com").orElseThrow();
        User receiver = userRepository.findByEmail("test2@example.com").orElseThrow();

        // Create a high-amount transaction to trigger fraud detection
        TransactionRequest highAmountRequest = new TransactionRequest(
            sender.getId(), receiver.getId(), 150000.0
        );

        MvcResult result = mockMvc.perform(post("/api/transactions/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(highAmountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fraudRiskScore").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        TransactionResponse response = objectMapper.readValue(responseContent, TransactionResponse.class);
        
        // High amount should result in a higher fraud score
        assertTrue(response.getFraudRiskScore() > 0);
    }
}