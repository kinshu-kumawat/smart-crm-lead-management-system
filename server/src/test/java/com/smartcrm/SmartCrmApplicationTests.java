package com.smartcrm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SmartCrmApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring application context loads cleanly with all beans and configurations
    }
}
