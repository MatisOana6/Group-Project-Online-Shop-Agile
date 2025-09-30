package com.backend;

import com.backend.dto.DeliveryDTO;
import com.backend.dto.OrderConfirmationDTO;
import com.backend.dto.OrderRequestDTO;
import com.backend.dto.ProductOrderDTO;
import com.backend.entity.*;
import com.backend.mapper.OrderItemMapper;
import com.backend.mapper.OrderMapper;
import com.backend.repository.OrderRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.UserRepository;
import com.backend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    UUID userId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    UUID sellerId = UUID.randomUUID();

    @Test
    void testPlaceOrder_success() {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(productId, 2);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj Napoca", "Cluj", "0111234"); // completează dacă e nevoie
        OrderRequestDTO request = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        User seller = new User();
        seller.setIdUser(sellerId);
        seller.setRole(Role.SELLER);

        Product product = new Product();
        product.setIdProduct(productId);
        product.setName("Test Product");
        product.setQuantity(10);
        product.setSeller(seller); // Seller with ID

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setPrice(100.0);
        orderItem.setStatus(OrderItemStatus.PENDING);

        User user = new User();
        user.setIdUser(userId);

        Order order = new Order();
        order.setIdOrder(UUID.randomUUID());
        order.setOrderItems(List.of(orderItem));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderItemMapper.fromDTO(productOrderDTO, product)).thenReturn(orderItem);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderMapper.fromRequestDTO(any(), any(), anyList(), anyDouble())).thenReturn(order);
        when(orderRepository.save(any())).thenReturn(order);

        // Act
        OrderConfirmationDTO result = orderService.placeOrder(request, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Order placed successfully.", result.getMessage());
        verify(messagingTemplate).convertAndSend(
                eq("/topic/seller/" + sellerId + "/notifications"),
                anyString()
        );
    }

    @Test
    void testPlaceOrder_productNotFound() {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(productId, 2);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj Napoca", "Cluj", "0111234"); // completează dacă e nevoie
        OrderRequestDTO request = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(request, userId));
    }

    @Test
    void testPlaceOrder_insufficientStock() {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(productId, 20);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj Napoca", "Cluj", "0111234"); // completează dacă e nevoie
        OrderRequestDTO request = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        Product product = new Product();
        product.setIdProduct(productId);
        product.setName("Test Product");
        product.setQuantity(5); // less than requested

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(request, userId));
    }

    @Test
    void testPlaceOrder_userNotFound() {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO(productId, 1);
        DeliveryDTO deliveryDTO = new DeliveryDTO("Cluj Napoca", "Cluj", "0111234"); // completează dacă e nevoie
        OrderRequestDTO request = new OrderRequestDTO(List.of(productOrderDTO), deliveryDTO);

        User seller = new User();
        seller.setIdUser(sellerId);
        seller.setRole(Role.SELLER);

        Product product = new Product();
        product.setIdProduct(productId);
        product.setName("Test Product");
        product.setQuantity(10);
        product.setSeller(seller);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setPrice(100.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderItemMapper.fromDTO(productOrderDTO, product)).thenReturn(orderItem);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(request, userId));
    }

}
