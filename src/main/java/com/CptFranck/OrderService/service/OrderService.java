package com.CptFranck.OrderService.service;

import com.CptFranck.OrderService.client.InventoryServiceClient;
import com.CptFranck.OrderService.entity.OrderEntity;
import com.CptFranck.OrderService.repository.OrderRepository;
import example.CptFranck.BookingService.dto.BookingEvent;
import example.CptFranck.InventoryService.dto.dto.BookingConfirmed;
import example.CptFranck.InventoryService.dto.dto.BookingRejected;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryServiceClient inventoryServiceClient;

    public OrderService(OrderRepository orderRepository, InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @KafkaListener(topics = "booking", groupId = "order-service")
    public void orderEvent(
//            com.CptFranck.BookingService.dto.BookingEvent
              BookingEvent bookingEvent) {
        log.info("Booking event received: " + bookingEvent);

        OrderEntity orderEntity = createOrder(bookingEvent);
        orderRepository.save(orderEntity);

        inventoryServiceClient.updateInventory(orderEntity.getEventId(), orderEntity.getTicketCount());
        log.info("Inventory updated for event: {}, less tickets: {}", orderEntity.getEventId(), orderEntity.getTicketCount());
    }

    private OrderEntity createOrder(BookingEvent bookingEvent) {
        return OrderEntity.builder()
                .customerId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    @KafkaListener(topics = "booking-confirmed", groupId = "order-service")
    public void handleBookingConfirmed(
//            com.CptFranck.InventoryService.dto.BookingConfirmed
            BookingConfirmed bookingConfirmed) {
        OrderEntity order = OrderEntity.builder()
                .customerId(bookingConfirmed.getUserId())
                .eventId(bookingConfirmed.getEventId())
                .ticketCount(bookingConfirmed.getTicketCount())
                .totalPrice(bookingConfirmed.getTotalPrice())
                .build();
        orderRepository.save(order);
        log.info("Order created: {}", order);
    }

    @KafkaListener(topics = "booking-rejected", groupId = "order-service")
    public void handleBookingRejected(
//            com.CptFranck.InventoryService.dto.BookingRejected
              BookingRejected bookingRejected) {
        log.warn("Booking rejected for user {}: {}", bookingRejected.getUserId(), bookingRejected.getReason());
    }
}
