package com.company.clawboard;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base integration test class with common configurations
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Common integration test utilities and configurations can be added here
}
