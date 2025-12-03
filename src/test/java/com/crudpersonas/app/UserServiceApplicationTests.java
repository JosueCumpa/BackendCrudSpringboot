package com.crudpersonas.app;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodRuns() {
        assertDoesNotThrow(() -> 
            UserServiceApplication.main(new String[]{})
        );
    }
}
