package com.backend;

import com.backend.controller.OrderController;
import com.backend.dto.DeliveryDTO;
import com.backend.dto.OrderConfirmationDTO;
import com.backend.dto.OrderRequestDTO;
import com.backend.dto.ProductOrderDTO;
import com.backend.service.JwtService;
import com.backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.TestExceptionHandler.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    @WithMockUser(roles = "CLIENT")
    @Test
    void placeOrder_shouldReturn200_whenValidRequest() throws Exception {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(productId, 2);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj Napoca", "Cluj", "0111234");
        OrderRequestDTO orderRequest = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        OrderConfirmationDTO confirmationDTO = new OrderConfirmationDTO(
                UUID.randomUUID(), 100.0, "Order placed successfully"
        );

        when(jwtService.extractId("Bearer faketoken")).thenReturn(userId);
        when(orderService.placeOrder(any(OrderRequestDTO.class), eq(userId))).thenReturn(confirmationDTO);

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer faketoken")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order placed successfully"))
                .andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void placeOrder_shouldReturn500_whenJwtFails() throws Exception {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(UUID.randomUUID(), 1);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj", "CJ", "0111234");
        OrderRequestDTO orderRequest = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        when(jwtService.extractId("Bearer faketoken")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer faketoken")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Invalid token"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void placeOrder_shouldReturn500_whenStockIsInsufficient() throws Exception {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(UUID.randomUUID(), 99);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj", "CJ", "0111234");
        OrderRequestDTO orderRequest = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        when(jwtService.extractId("Bearer faketoken")).thenReturn(UUID.randomUUID());
        when(orderService.placeOrder(any(), any())).thenThrow(new RuntimeException("Insufficient stock"));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer faketoken")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isInternalServerError())
        .andExpect(content().string("Insufficient stock"));
    }


    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handle(RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
