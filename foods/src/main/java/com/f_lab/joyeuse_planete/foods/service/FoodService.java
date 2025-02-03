package com.f_lab.joyeuse_planete.foods.service;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.repository.FoodRepository;
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

  public FoodDTO getFood(Long foodId) {
    return FoodDTO.from(findFood(foodId));
  }

  @Transactional
  public void deleteFood(Long foodId) {
    foodRepository.delete(findFood(foodId));
  }

  @Transactional
  public void reserve(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);
    food.minusQuantity(quantity);
    foodRepository.save(food);
  }

  @Transactional
  public void release(Long foodId, int quantity) {
    Food food = findFoodWithLock(foodId);
    food.plusQuantity(quantity);
    foodRepository.save(food);
  }

  private Food findFood(Long foodId) {
    return foodRepository.findById(foodId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }

  private Food findFoodWithLock(Long foodId) {
    return foodRepository.findFoodByFoodIdWithPessimisticLock(foodId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.FOOD_NOT_EXIST_EXCEPTION));
  }
}
