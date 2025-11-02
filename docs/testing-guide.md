# 🧪 Testing Guide

This guide covers unit testing and integration testing strategies for the Pet Store application.

## 📋 Table of Contents
- [Test Structure](#-test-structure)
- [Unit Testing](#-unit-testing)
- [Integration Testing](#-integration-testing)
- [Running Tests](#-running-tests)
- [Test Configuration](#-test-configuration)
- [Writing New Tests](#-writing-new-tests)
- [Best Practices](#-best-practices)

---

## Test Structure

```
pet-store-api/src/test/
├── java/com/petstore/
│   ├── controller/          # Controller unit tests
│   ├── service/             # Service unit tests
│   ├── repository/          # Repository tests
│   ├── exception/           # Exception tests
│   ├── config/              # Configuration tests
│   └── integration/         # Integration tests
└── resources/
    └── application-test.properties
```

---

## Unit Testing

Unit tests focus on testing individual components in isolation using mocks.

### Example: Service Layer Test

```java
@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    
    @Mock
    private PetRepository petRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private PetService petService;
    
    @Test
    void testCreatePet_Success() {
        // Arrange
        PetRequest request = new PetRequest();
        request.setName("Buddy");
        request.setPrice(new BigDecimal("500.00"));
        
        Category category = new Category();
        category.setId(1L);
        
        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));
        when(petRepository.save(any(Pet.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Pet result = petService.createPet(request, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        verify(petRepository).save(any(Pet.class));
    }
}
```

### Key Annotations
- `@ExtendWith(MockitoExtension.class)` - Enables Mockito support
- `@Mock` - Creates a mock instance
- `@InjectMocks` - Creates an instance and injects mocks into it
- `@BeforeEach` - Runs before each test method

---

---

## Integration Testing

Integration tests verify that different parts of the application work together correctly.

### Test Infrastructure

#### BaseIntegrationTest

All integration tests extend `BaseIntegrationTest` which provides:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    protected User testUser;
    protected User testAdmin;
    protected String userToken;
    protected String adminToken;
    
    // Helper methods for authentication
    protected String createAuthorizationHeader(String token) {
        return "Bearer " + token;
    }
}
```

**Key Features:**
- ✅ **Real application context** - Full Spring Boot application starts
- ✅ **H2 in-memory database** - Fresh database for each test class
- ✅ **MockMvc** - Simulates HTTP requests without starting server
- ✅ **Transactional rollback** - Database changes are rolled back after each test
- ✅ **JWT authentication** - Pre-configured test users with tokens

### Test Configuration

#### application-test.properties

```properties
# H2 In-Memory Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Auto-create tables from entities
spring.jpa.hibernate.ddl-auto=create-drop

# Random port to avoid conflicts
server.port=0

# JWT settings for testing
jwt.secret=test-secret-key-for-jwt-tokens
jwt.expiration=3600000
```

### Example Integration Test

```java
@Test
void testCreatePet_Success() throws Exception {
    // Prepare request data
    Map<String, Object> petRequest = new HashMap<>();
    petRequest.put("name", "Max");
    petRequest.put("description", "Friendly dog");
    petRequest.put("price", 450.00);
    petRequest.put("categoryId", testCategory.getId());
    
    // Execute request and verify response
    mockMvc.perform(post("/api/pets")
            .header("Authorization", createAuthorizationHeader(userToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(petRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Max"))
            .andExpect(jsonPath("$.price").value(450.00))
            .andExpect(jsonPath("$.status").value("AVAILABLE"));
}
```

### Test Scenarios Covered

#### ✅ CRUD Operations
- Create, Read, Update, Delete pets
- Pagination and filtering
- Search functionality

#### ✅ Authentication & Authorization
- Unauthorized access (no token)
- Regular user permissions
- Admin-only operations

#### ✅ Validation
- Invalid input data
- Missing required fields
- Business rule validation

#### ✅ Error Handling
- 404 Not Found
- 403 Forbidden
- 400 Bad Request

---

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Only Unit Tests
```bash
mvn test -Dtest=*Test
```

### Run Only Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Run Specific Test Class
```bash
mvn test -Dtest=PetIntegrationTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=PetIntegrationTest#testCreatePet_Success
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
# View report at: target/site/jacoco/index.html
```

### Run Tests in VS Code
1. Click on the test class or method
2. Click "Run Test" or "Debug Test" in the gutter
3. View results in the Test Explorer panel

---

## Test Configuration

### Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 5 (included in spring-boot-starter-test) -->
    <!-- Mockito (included in spring-boot-starter-test) -->
    <!-- AssertJ (included in spring-boot-starter-test) -->
</dependencies>
```

---

## Writing New Tests

### 1. Create Integration Test Class

```java
package com.petstore.integration;

import com.petstore.model.YourEntity;
import com.petstore.repository.YourRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class YourEntityIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private YourRepository repository;
    
    private YourEntity testEntity;
    
    @BeforeEach
    void setUp() {
        // Create test data
        testEntity = new YourEntity();
        testEntity.setName("Test");
        testEntity = repository.save(testEntity);
    }
    
    @Test
    void testGetEntity_Success() throws Exception {
        mockMvc.perform(get("/api/entities/{id}", testEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }
    
    @Test
    void testCreateEntity_WithAuth() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "New Entity");
        
        mockMvc.perform(post("/api/entities")
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Entity"));
    }
}
```

### 2. Common Test Patterns

#### Testing Pagination
```java
@Test
void testGetAll_WithPagination() throws Exception {
    mockMvc.perform(get("/api/entities")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "name,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").exists())
            .andExpect(jsonPath("$.totalPages").exists());
}
```

#### Testing Authentication
```java
@Test
void testProtectedEndpoint_Unauthorized() throws Exception {
    mockMvc.perform(post("/api/entities"))
            .andExpect(status().isUnauthorized());
}

@Test
void testProtectedEndpoint_Authorized() throws Exception {
    mockMvc.perform(post("/api/entities")
            .header("Authorization", createAuthorizationHeader(userToken)))
            .andExpect(status().isOk());
}
```

#### Testing Authorization (Admin Only)
```java
@Test
void testAdminEndpoint_AsUser() throws Exception {
    mockMvc.perform(delete("/api/entities/{id}", 1L)
            .header("Authorization", createAuthorizationHeader(userToken)))
            .andExpect(status().isForbidden());
}

@Test
void testAdminEndpoint_AsAdmin() throws Exception {
    mockMvc.perform(delete("/api/entities/{id}", 1L)
            .header("Authorization", createAuthorizationHeader(adminToken)))
            .andExpect(status().isNoContent());
}
```

#### Testing Validation
```java
@Test
void testCreate_InvalidData() throws Exception {
    Map<String, Object> request = new HashMap<>();
    request.put("name", ""); // Empty name (invalid)
    
    mockMvc.perform(post("/api/entities")
            .header("Authorization", createAuthorizationHeader(userToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
}
```

---

## Best Practices

### ✅ DO

1. **Extend BaseIntegrationTest** for all integration tests
   - Gets authentication, database setup automatically

2. **Use @BeforeEach for test data**
   ```java
   @BeforeEach
   void setUp() {
       // Create fresh test data for each test
   }
   ```

3. **Test both success and failure paths**
   ```java
   @Test void testSuccess() { ... }
   @Test void testNotFound() { ... }
   @Test void testUnauthorized() { ... }
   ```

4. **Use descriptive test names**
   ```java
   @Test
   void testCreatePet_WithValidData_ReturnsCreated() { ... }
   
   @Test
   void testDeletePet_AsRegularUser_ReturnsForbidden() { ... }
   ```

5. **Verify database state when needed**
   ```java
   Pet savedPet = petRepository.findById(petId).orElseThrow();
   assertEquals("Updated Name", savedPet.getName());
   ```

6. **Use Hamcrest matchers for complex assertions**
   ```java
   .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
   .andExpect(jsonPath("$.items[*].price", everyItem(greaterThan(0.0))))
   ```

### ❌ DON'T

1. **Don't share state between tests**
   - Each test should be independent
   - Use @BeforeEach to reset state

2. **Don't hardcode IDs from database**
   - Database is wiped between test runs
   - Always save entities first and use their IDs

3. **Don't skip cleanup**
   - `@Transactional` handles this automatically
   - Data is rolled back after each test

4. **Don't test Spring Boot internals**
   - Focus on your business logic
   - Trust that Spring framework works

5. **Don't create integration tests for everything**
   - Use unit tests for complex business logic
   - Use integration tests for API endpoints and database operations

---

## Test Coverage Goals

| Layer | Target Coverage |
|-------|----------------|
| Controllers | 80%+ |
| Services | 90%+ |
| Repositories | 70%+ (Spring Data methods don't need testing) |
| Models | 60%+ (simple POJOs need less coverage) |
| Overall | 75%+ |

---

## Debugging Tests

### Enable SQL Logging
Add to `application-test.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Print Request/Response
```java
mockMvc.perform(get("/api/pets"))
        .andDo(print())  // Prints full request/response
        .andExpect(status().isOk());
```

### Use VS Code Debugger
1. Set breakpoints in test code
2. Right-click test → "Debug Test"
3. Step through code to find issues

---

## Additional Resources

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

## 🎓 Summary

✅ **Unit Tests** - Fast, isolated, mock dependencies  
✅ **Integration Tests** - Slower, realistic, test full flow  
✅ **BaseIntegrationTest** - Provides authentication and database setup  
✅ **@Transactional** - Automatic database rollback  
✅ **MockMvc** - HTTP request simulation without server  
✅ **H2 Database** - In-memory database for tests  

**Run tests frequently** to catch bugs early and ensure code quality! 🚀
