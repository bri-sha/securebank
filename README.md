# 🏦 SecureBank - Digital Banking Backend with Fraud Detection

SecureBank is a Java + Spring Boot based backend system designed to process digital bank transactions securely, while also detecting and scoring potential fraud using intelligent algorithms. It offers JWT-based authentication, robust REST APIs, and an integrated FraudScoringService using real-time transaction data and graph-based logic.

## 🧱 Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.3
- **Database**: MySQL 8.0
- **Security**: Spring Security + JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **API Testing**: Postman

## 🚀 Key Features

### ✅ 1. User Registration & Authentication (JWT)
- Secure login/register APIs using Spring Security and JWT
- Access control to protected endpoints
- Password encryption using BCrypt

### 💸 2. Transaction Management
- Create new transactions between users
- Store amount, sender, receiver, timestamp, and fraud risk score
- Comprehensive transaction querying capabilities

### 🕵️‍♂️ 3. Fraud Detection Engine
- Modular FraudScoringService calculates a score (0–10) using:
  - **Transaction Amount Analysis**: Higher amounts = higher risk
  - **Velocity Detection**: Rapid consecutive transfers detection
  - **Out-degree Analysis**: Suspicious spread patterns from sender
  - **Cycle Detection in Fraud Graph**: Graph-based fraud patterns

### 📈 4. FraudGraph Engine
- Each transaction is a directed edge in a dynamic user graph
- Cycles are red flags for money laundering or fraud rings
- Real-time graph analysis for fraud pattern detection

## 🛠️ Setup Instructions

### Prerequisites
- Java 21 or higher
- MySQL 8.0
- Maven 3.6+

### 1. Database Setup
Create a MySQL database:
```sql
CREATE DATABASE securebank_db;
CREATE USER 'root'@'localhost' IDENTIFIED BY '<your password>';
GRANT ALL PRIVILEGES ON securebank_db.* TO 'username'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration
Set the following environment variables before running the project:
    $env:DB_NAME="securebank_db"
    $env:DB_USER="username"
    $env:DB_PASSWORD="yourStrongPassword"
    $env:SERVER_PORT="8080"

Update `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/securebank_db
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
server.port=${SERVER_PORT:8080}
```

### 3. Build and Run
```bash
# Clone the repository
git clone <repository-url>
cd securebank

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📊 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securepassword123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "john@example.com",
    "password": "securepassword123"
}
```
**Response:**
```json
{
    "message": "Login successful",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Get User Profile
```http
GET /api/auth/profile
Authorization: Bearer <jwt-token>
```

### Transaction Endpoints

#### Create Transaction
```http
POST /api/transactions/create
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
    "senderId": 1,
    "receiverId": 2,
    "amount": 1000.00
}
```
**Response:**
```json
{
    "id": 1,
    "senderId": 1,
    "senderEmail": "sender@example.com",
    "receiverId": 2,
    "receiverEmail": "receiver@example.com",
    "amount": 1000.00,
    "timestamp": "2024-01-15T10:30:00",
    "fraudRiskScore": 3,
    "status": "LOW_RISK"
}
```

#### Get Transactions by Sender
```http
GET /api/transactions/sender/{senderId}
Authorization: Bearer <jwt-token>
```

#### Get Transactions by Receiver
```http
GET /api/transactions/receiver/{receiverId}
Authorization: Bearer <jwt-token>
```

#### Get Transaction by ID
```http
GET /api/transactions/{transactionId}
Authorization: Bearer <jwt-token>
```

#### Get All Transactions (Admin)
```http
GET /api/transactions/all
Authorization: Bearer <jwt-token>
```

## 🧠 Fraud Detection Logic

The fraud scoring system analyzes multiple factors:

### 1. Amount Score (1-3 points)
- Small amounts (< $50,000): 1 point
- Medium amounts ($50,000 - $100,000): 2 points
- Large amounts (> $100,000): 3 points

### 2. Velocity Score (1-3 points)
- Time gap > 5 minutes: 1 point
- Time gap 1-5 minutes: 2 points
- Time gap < 1 minute: 3 points

### 3. Cycle Detection (0 or 3 points)
- No suspicious cycles: 0 points
- Suspicious cycle detected: 3 points

### 4. Out-degree Score (1-3 points)
- Few unique receivers (< 5): 1 point
- Moderate receivers (5-10): 2 points
- Many receivers (> 10): 3 points

### Risk Categories
- **LOW_RISK**: Score 1-4
- **MEDIUM_RISK**: Score 5-7
- **HIGH_RISK**: Score 8-10

## 🧾 Usage Flow

1. **Register** → Create a new user account
2. **Login** → Get JWT token for authentication
3. **Create Transaction** → Send money between users
4. **Fraud Score Calculation** → Automatic risk assessment
5. **View Transactions** → Query transaction history

## 🎯 Future Enhancements

- [ ] Real-time fraud alert system
- [ ] Admin dashboard for monitoring flagged accounts
- [ ] AI/ML model integration for predictive fraud detection
- [ ] Transaction pattern heatmaps
- [ ] Role-based access control (RBAC)
- [ ] Transaction limits and daily caps
- [ ] Email notifications for high-risk transactions
- [ ] Transaction reversal capabilities
- [ ] Advanced analytics and reporting

## 🔐 Security Features

- JWT-based stateless authentication
- Password encryption using BCrypt
- Protected endpoints with Spring Security
- Input validation and sanitization
- SQL injection prevention with JPA

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📞 API Testing with Postman

1. Import the provided Postman collection
2. Set up environment variables:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (obtained from login response)

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL is running
   - Check database credentials in `application.properties`

2. **JWT Token Expired**
   - Tokens expire after 10 hours
   - Re-login to get a new token

3. **Port Already in Use**
   - Change the port in `application.properties`: `server.port=8081`

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

**Happy Banking! 🏦✨**
