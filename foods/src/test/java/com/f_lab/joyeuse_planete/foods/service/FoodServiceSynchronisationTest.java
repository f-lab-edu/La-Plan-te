package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FoodServiceSynchronisationTest {

  @Autowired
  FoodService foodService;

  @Autowired
  FoodRepository foodRepository;

  Food dummyFood = Food.builder()
      .price(BigDecimal.valueOf(1000))
      .totalQuantity(1000)
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
          foodService.reserve(1L, 10);
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