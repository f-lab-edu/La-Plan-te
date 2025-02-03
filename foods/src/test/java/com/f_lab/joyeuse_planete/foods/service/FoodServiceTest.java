package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.domain.Currency;
import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.domain.Store;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

  @InjectMocks
  FoodService foodService;
  @Mock
  FoodRepository foodRepository;

  @Test
  @DisplayName("getFood 호출시 성공")
  void testGetFoodSuccess() {
    Long foodId = 1L;
    Food food = createFood(foodId);
    FoodDTO expected = createExpectedFoodDTO(food);

    // when
    when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
    FoodDTO result = foodService.getFood(foodId);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("존재하지 않는 food 를 getFood 통해서 호출시 실패")
  void testGetFoodOnNotExistingFoodFail() {
    // given
    Long foodId = 1L;

    // when
    when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> foodService.getFood(foodId))
        .isInstanceOf(JoyeusePlaneteApplicationException.class)
        .hasMessage(ErrorCode.FOOD_NOT_EXIST_EXCEPTION.getDescription());
  }

  @Test
  @DisplayName("deleteFood() 호출시 성공")
  void deleteFoodSuccess() {
    // Given
    Long foodId = 1L;
    Food food = createFood(foodId);

    when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

    // When
    foodService.deleteFood(foodId);

    // Then
    verify(foodRepository).delete(food);
  }

  @Test
  @DisplayName("reserve() 호출시 성공")
  void testReserveSuccess() {
    // given
    Long foodId = 1L;
    int quantity = 1;
    Food food = createFood(foodId);
    int totalQuantity = food.getTotalQuantity();

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong())).thenReturn(Optional.of(food));
    foodService.reserve(foodId, quantity);

    // then
    assertThat(food.getTotalQuantity()).isEqualTo(totalQuantity - quantity);
  }

  @Test
  @DisplayName("release() 호출시 성공")
  void testReleaseSuccess() {
    // given
    Long foodId = 1L;
    int quantity = 1;
    Food food = createFood(foodId);
    int totalQuantity = food.getTotalQuantity();

    // when
    when(foodRepository.findFoodByFoodIdWithPessimisticLock(anyLong())).thenReturn(Optional.of(food));
    foodService.release(foodId, quantity);

    // then
    assertThat(food.getTotalQuantity()).isEqualTo(totalQuantity + quantity);
  }

  private Food createFood(Long foodId) {
    Long storeId = 1L;
    Long currencyId = 100L;
    String foodName = "Pizza";
    BigDecimal price = new BigDecimal("9.99");
    int totalQuantity = 50;
    String currencyCode = "USD";
    String currencySymbol = "$";

    Store store = Store.builder()
        .id(storeId)
        .build();

    Currency currency = Currency.builder()
        .id(currencyId)
        .currencyCode(currencyCode)
        .currencySymbol(currencySymbol)
        .build();

    return Food.builder()
        .id(foodId)
        .store(store)
        .currency(currency)
        .foodName(foodName)
        .price(price)
        .totalQuantity(totalQuantity)
        .build();
  }

  private FoodDTO createExpectedFoodDTO(Food food) {
    return FoodDTO.builder()
        .foodId(food.getId())
        .storeId(food.getStore().getId())
        .currencyId(food.getCurrency().getId())
        .foodName(food.getFoodName())
        .price(food.getPrice())
        .totalQuantity(food.getTotalQuantity())
        .currencyCode(food.getCurrency().getCurrencyCode())
        .currencySymbol(food.getCurrency().getCurrencySymbol())
        .build();
  }
}