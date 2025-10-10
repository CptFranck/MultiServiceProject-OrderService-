package com.CptFranck.OrderService.service;

import com.CptFranck.OrderService.entity.OrderEntity;
import com.CptFranck.OrderService.repository.OrderRepository;
import com.CptFranck.dto.BookingConfirmed;
import com.CptFranck.dto.BookingRejected;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "booking-confirmed", groupId = "order-service")
    public void handleBookingConfirmed(BookingConfirmed event) {
        log.info("Received booking confirmed: {}", event);

        OrderEntity order = OrderEntity.builder()
                .customerId(event.getUserId())
                .eventId(event.getEventId())
                .ticketCount(event.getTicketCount())
                .totalPrice(event.getTotalPrice())
                .build();
        orderRepository.save(order);

        log.info("Order created: {}", order);
    }

    @KafkaListener(topics = "booking-rejected", groupId = "order-service")
    public void handleBookingRejected(BookingRejected event) {
        log.info("Received booking rejected: {}", event);

        log.warn("Booking rejected for user {}: {}", event.getUserId(), event.getReason());
    }
}
