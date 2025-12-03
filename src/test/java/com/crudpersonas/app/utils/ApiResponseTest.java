package com.crudpersonas.app.utils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiResponseTest {
    @Test
    void constructorAndGetMessage() {
        String testMessage = "Mensaje de prueba";
        ApiResponse response = new ApiResponse(testMessage);
        
        assertEquals(testMessage, response.getMessage());
    }

    @Test
    void setMessage() {
        ApiResponse response = new ApiResponse("Mensaje inicial");
        String newMessage = "Actualizacion de mensaje";
        
        response.setMessage(newMessage);
        
        assertEquals(newMessage, response.getMessage());
    }
}
