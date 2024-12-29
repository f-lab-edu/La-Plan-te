package com.f_lab.la_planete.service;

import com.f_lab.la_planete.domain.Food;
import com.f_lab.la_planete.dto.request.OrderCreateRequestDTO;
import com.f_lab.la_planete.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceSynchronisationTest {

  @Autowired
  OrderService orderService;

  @Autowired
  FoodRepository foodRepository;

  Food dummyFood = Food.builder()
      .price(BigDecimal.valueOf(1000))
      .totalQuantity(1000)
      .build();

  OrderCreateRequestDTO request = OrderCreateRequestDTO.builder()
      .foodId(1L)
      .price(BigDecimal.valueOf(1000))
      .quantity(10)
      .build();

  @BeforeEach
  void beforeEach() {
    foodRepository.saveAndFlush(dummyFood);
  }

  @Test
  @DisplayName("동시성 테스트 100개의 요청이 동시에 왔을 때 데이터의 일관성이 유지")
  void test_concurrency_thread_100_success() throws InterruptedException {
    // given
    int count = 100;
    CountDownLatch countDownLatch = new CountDownLatch(count);
    ExecutorService executorService = Executors.newFixedThreadPool(count);

    // when
    for (int i = 0; i < count; i++) {
      executorService.submit(() -> {
        try {
          orderService.createFoodOrder(request);
        } finally {
          countDownLatch.countDown();
        }
      });
    }

    countDownLatch.await();

    // then
    Food food = foodRepository.findById(1L).orElseThrow();
    assertThat(food.getTotalQuantity()).isEqualTo(0L);
  }
}