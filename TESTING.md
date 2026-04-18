# ClawBoard Test Suite

## Overview

This directory contains comprehensive unit and integration tests for the ClawBoard application.

## Test Structure

```
src/test/java/com/company/clawboard/
├── BaseUnitTest.java                    # Base class for unit tests
├── BaseIntegrationTest.java             # Base class for integration tests
├── controller/
│   ├── DashboardControllerTest.java     # Unit tests for DashboardController
│   ├── DashboardControllerIntegrationTest.java  # Integration tests
│   ├── TurnErrorControllerTest.java     # Unit tests for TurnErrorController
│   ├── TurnErrorControllerIntegrationTest.java  # Integration tests
│   └── ScanControllerIntegrationTest.java       # Integration tests for ScanController
├── service/
│   ├── DashboardServiceTest.java        # Unit tests for DashboardService
│   └── TurnErrorServiceTest.java        # Unit tests for TurnErrorService
└── mapper/
    └── EmployeeMapperTest.java          # Integration tests for EmployeeMapper

src/test/resources/
└── application-test.yml                 # Test profile configuration (H2 database)
```

## Test Types

### 1. Unit Tests
- **Purpose**: Test individual components in isolation
- **Framework**: JUnit 5 + Mockito
- **Location**: `*Test.java` files in `service/` and `controller/`
- **Features**:
  - Mock dependencies using `@Mock`
  - Fast execution (no database or Spring context)
  - Focus on business logic validation

### 2. Integration Tests
- **Purpose**: Test components working together with real dependencies
- **Framework**: Spring Boot Test + H2 Database
- **Location**: `*IntegrationTest.java` files
- **Features**:
  - Full Spring context loading
  - H2 in-memory database for data layer testing
  - MockMvc for HTTP endpoint testing
  - Database schema initialization via SQL scripts

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Only Unit Tests
```bash
mvn test -Dtest="*Test" -DfailIfNoTests=false
```

### Run Only Integration Tests
```bash
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
```

### Run Specific Test Class
```bash
mvn test -Dtest=DashboardServiceTest
mvn test -Dtest=DashboardControllerIntegrationTest
```

### Run with Verbose Output
```bash
mvn test -X
```

### Run with Coverage Report
```bash
mvn clean test jacoco:report
```
(Requires adding Jacoco plugin to pom.xml)

## Test Configuration

### Test Profile (`application-test.yml`)
- Uses H2 in-memory database (MySQL compatible mode)
- Disables Flyway migrations (uses direct SQL initialization)
- Enables MyBatis logging for debugging
- Disables scheduled scans during tests

### Database Schema
Tests use the same schema as production, initialized from:
```
src/main/resources/db/migration/V1__init_schema_clean.sql
```

## Writing New Tests

### Unit Test Template
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("MyService Unit Tests")
class MyServiceTest {

    @Mock
    private DependencyMapper dependencyMapper;

    private MyService myService;

    @BeforeEach
    void setUp() {
        myService = new MyService(dependencyMapper);
    }

    @Test
    @DisplayName("Should do something")
    void testSomething() {
        // Given
        when(dependencyMapper.someMethod()).thenReturn(expectedValue);

        // When
        Result result = myService.doSomething();

        // Then
        assertThat(result).isNotNull();
        verify(dependencyMapper, times(1)).someMethod();
    }
}
```

### Integration Test Template
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("MyController Integration Tests")
class MyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/endpoint should return 200")
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/endpoint")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

### Mapper Test Template
```java
@MybatisTest
@ActiveProfiles("test")
@DisplayName("MyMapper Integration Tests")
class MyMapperTest {

    @Autowired
    private MyMapper myMapper;

    @Test
    @Sql(statements = {
        "INSERT INTO table_name (col1, col2) VALUES ('val1', 'val2')"
    })
    void testSelect() {
        // When
        List<Entity> results = myMapper.selectAll();

        // Then
        assertThat(results).hasSize(1);
    }
}
```

## Best Practices

1. **Test Naming**: Use descriptive names with `@DisplayName` annotations
2. **AAA Pattern**: Arrange-Act-Assert structure in all tests
3. **Assertions**: Use AssertJ for fluent, readable assertions
4. **Isolation**: Each test should be independent and not rely on other tests
5. **Cleanup**: Use `@BeforeEach` to reset state before each test
6. **Mock Verification**: Verify mock interactions when testing behavior
7. **Edge Cases**: Test null inputs, empty collections, and error conditions
8. **Documentation**: Add JavaDoc comments explaining test purpose

## Common Issues

### H2 Database Compatibility
- H2 runs in MySQL compatibility mode: `MODE=MySQL`
- Some MySQL-specific features may not work exactly the same
- Test queries should be compatible with both MySQL and H2

### XML Special Characters
- Remember to escape special characters in MyBatis XML mappers:
  - `>=` → `&gt;=`
  - `<=` → `&lt;=`
  - `<` → `&lt;`
  - `>` → `&gt;`
  - `&` → `&amp;`

### Test Data Management
- Use `@Sql` annotations for test data setup
- Clean up data in `@BeforeEach` to ensure test isolation
- Avoid sharing state between tests

## Continuous Integration

Tests are designed to run in CI/CD pipelines:
- Fast unit tests run on every commit
- Integration tests run on PR merges
- All tests must pass before deployment

## Future Enhancements

Consider adding:
- [ ] Test coverage reports with JaCoCo
- [ ] Performance tests for critical paths
- [ ] Contract tests for API endpoints
- [ ] End-to-end tests with Testcontainers + MySQL
- [ ] Mutation testing with PITest
- [ ] Property-based testing with jqwik
