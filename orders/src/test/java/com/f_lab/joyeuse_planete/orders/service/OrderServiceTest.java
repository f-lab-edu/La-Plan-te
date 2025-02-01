package com.f_lab.joyeuse_planete.orders.service;

import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.orders.domain.OrderSearchCondition;
import com.f_lab.joyeuse_planete.orders.dto.response.OrderDTO;
import com.f_lab.joyeuse_planete.orders.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @InjectMocks
  OrderService orderService;
  @Mock
  OrderRepository orderRepository;

  @Test
  @DisplayName("주문의 상태를 업데이트 성공")
  public void testUpdateOrderStatusSuccess() {
    // given
    Long orderId = 1L;
    OrderStatus status = OrderStatus.READY;
    Order expected = Order.builder().id(orderId).build();

    // when
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(expected));
    orderService.updateOrderStatus(orderId, status);

    // then
    assertThat(expected.getStatus()).isEqualTo(status);
  }

  @Test
  @DisplayName("존재 하지 않는 주문을 업데이트할 경우 실패")
  public void testUpdateInvalidOrderStatusFail() {
    // given
    Long orderId = 1L;
    OrderStatus status = OrderStatus.READY;

    // when
    when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, status))
        .isInstanceOf(JoyeusePlaneteApplicationException.class)
        .hasMessage(ErrorCode.ORDER_NOT_EXIST_EXCEPTION.getDescription());
  }

  @Test
  @DisplayName("orderService 가 올바로 orderRepository를 호출하고 Page를 return 하는 것을 확인")
  void testCallingOnOrderRepositoryAndReturnPageOrderDTO() {
    // given
    Page<OrderDTO> expected = Page.empty();
    OrderSearchCondition condition = new OrderSearchCondition();
    Pageable pageable = PageRequest.of(0, 10);

    // when
    when(orderRepository.findOrders(any(), any())).thenReturn(expected);
    Page<OrderDTO> result = orderService.findOrders(condition, pageable);

    // then
    assertThat(result).isEqualTo(expected);
    verify(orderRepository, times(1)).findOrders(condition, pageable);
  }
}