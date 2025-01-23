package com.f_lab.la_planete.foods.service;

import com.f_lab.la_planete.core.domain.Food;
import com.f_lab.la_planete.core.exceptions.ErrorCode;
import com.f_lab.la_planete.core.exceptions.ApplicationException;
import com.f_lab.la_planete.foods.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

  private final FoodRepository foodRepository;


  @Transactional
  public void reserve(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);
    food.minusQuantity(quantity);
    foodRepository.save(food);
  }

  private Food findFoodWithLock(Long foodId) {
    return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }
}
