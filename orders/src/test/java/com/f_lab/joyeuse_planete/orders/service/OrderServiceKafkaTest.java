package com.f_lab.joyeuse_planete.orders.service;


import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.kafka.exceptions.RetryableException;
import com.f_lab.joyeuse_planete.core.kafka.service.KafkaService;
import com.f_lab.joyeuse_planete.orders.dto.request.OrderCreateRequestDTO;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderCreateResponseDTO;
import com.f_lab.joyeuse_planete.orders.repository.OrderRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@EmbeddedKafka
@SpringBootTest
public class OrderServiceKafkaTest {

  @InjectMocks
  OrderService orderService;
  @Mock
  KafkaService kafkaService;
  @Mock
  OrderRepository orderRepository;

  @Test
  @DisplayName("주문 생성 메서드 호출 후 성공")
  void testCreateOrderSuccess() {
    // given
    OrderCreateResponseDTO expected = createOrderCreateResponseDTO("PROCESSING");
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();

    // when
    when(orderRepository.save(any(Order.class))).thenReturn(null);
    doNothing().when(kafkaService).sendKafkaEvent(anyString(), any(Object.class));
    OrderCreateResponseDTO response = orderService.createFoodOrder(request);

    // then
    assertThat(response.getMessage()).isEqualTo(expected.getMessage());
  }

  @Test
  @DisplayName("주문 생성 메서드 호출 후 데이터베이스 관련 에러로 인한 실패 1")
  void testCreateOrderRepositoryThrowExceptionFail1() {
    // given
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();

    // when
    when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException());

    // then
    assertThatThrownBy(() -> orderService.createFoodOrder(request))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("주문 생성 메서드 호출 후 데이터베이스 관련 에러로 인한 실패 2")
  void testCreateOrderRepositoryThrowExceptionFail2() {
    // given
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();

    // when
    when(orderRepository.save(any(Order.class))).thenThrow(new JoyeusePlaneteApplicationException());

    // then
    assertThatThrownBy(() -> orderService.createFoodOrder(request))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("주문 생성 메서드 호출 후 카프카 관련 에러로 인한 실패")
  void testCreateOrderRepositoryKafkaServiceFail() {
    // given
    OrderCreateRequestDTO request = createOrderCreateRequestDTO();

    // when
    when(orderRepository.save(any(Order.class))).thenReturn(null);
    doThrow(JoyeusePlaneteApplicationException.class).when(kafkaService).sendKafkaEvent(any(), any());

    // then
    assertThatThrownBy(() -> orderService.createFoodOrder(request))
        .isInstanceOf(JoyeusePlaneteApplicationException.class);
  }

  private OrderCreateRequestDTO createOrderCreateRequestDTO() {
    return OrderCreateRequestDTO.builder()
        .foodId(1L)
        .foodName("Test Food")
        .storeId(10L)
        .totalCost(new BigDecimal("19.99"))
        .quantity(2)
        .voucherId(200L)
        .paymentInformation(OrderCreateRequestDTO.PaymentInformation.builder()
            .cardNumber("1234-5678-9012-3456")
            .cardHolderName("John Doe")
            .expiryDate("12/25")
            .cvc("123")
            .build())
        .build();
  }

  private OrderCreateResponseDTO createOrderCreateResponseDTO(String message) {
    return new OrderCreateResponseDTO(message);
  }
}
