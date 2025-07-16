package org.example.backend.service;

import org.example.backend.dto.Order.OrderItemDTO;
import org.example.backend.dto.Order.OrderRequestDTO;
import org.example.backend.model.*;
import org.example.backend.model.enums.OrderStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuestTabRepository guestTabRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderSpecificationService orderSpecificationService;

    @InjectMocks
    private OrderService orderService;

    private User waiter;
    private GuestTab guestTab;
    private Product product;

    @BeforeEach
    void setup() {
        waiter = User.builder().id(UUID.randomUUID()).email("waiter@test.com").name("Carlos").build();
        guestTab = GuestTab.builder().id(1L).guestName("João").build();
        product = Product.builder().id(UUID.randomUUID()).name("Coca-Cola").price(10.0).build();
    }

    @Test
    void registerOrder_ShouldPersistOrders_WhenValidRequest() {
        Long guestTabId = 1L;
        UUID productId = UUID.randomUUID();

        OrderItemDTO item = new OrderItemDTO(2, "Sem gelo", productId);
        OrderRequestDTO request = new OrderRequestDTO(
                null,
                guestTabId,
                new OrderItemDTO[]{item},
                "waiter@test.com"
        );

        when(userRepository.findByEmail("waiter@test.com")).thenReturn(Optional.of(waiter));
        when(guestTabRepository.findById(guestTabId)).thenReturn(Optional.of(guestTab));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        boolean result = orderService.registerOrder(request);

        assertTrue(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    void registerOrder_ShouldThrowException_WhenWaiterNotFound() {
        OrderItemDTO item = new OrderItemDTO(1, "obs", UUID.randomUUID());
        OrderRequestDTO request = new OrderRequestDTO(
                null,
                1L,
                new OrderItemDTO[]{item},
                "notfound@test.com"
        );

        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.registerOrder(request));
    }

    @Test
    void advanceStatus_ShouldUpdateToNext_WhenValidFlow() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.SENT)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.advanceStatus(1L);

        assertEquals(OrderStatus.IN_PREPARE, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void regressStatus_ShouldUpdateToPrevious_WhenValidFlow() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.regressStatus(1L);

        assertEquals(OrderStatus.IN_PREPARE, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void advanceStatus_ShouldThrow_WhenAlreadyDelivered() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        var exception = assertThrows(RuntimeException.class, () -> orderService.advanceStatus(1L));
        assertTrue(exception.getMessage().contains("status final"));
    }

    @Test
    void regressStatus_ShouldThrow_WhenAlreadySent() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.SENT)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        var exception = assertThrows(RuntimeException.class, () -> orderService.regressStatus(1L));
        assertTrue(exception.getMessage().contains("status final"));
    }
}
