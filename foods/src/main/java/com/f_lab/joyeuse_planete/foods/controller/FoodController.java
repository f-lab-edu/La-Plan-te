package com.f_lab.joyeuse_planete.foods.controller;

import com.f_lab.joyeuse_planete.foods.dto.response.FoodDTO;
import com.f_lab.joyeuse_planete.foods.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodController {

  private final FoodService foodService;

  @GetMapping("/{foodId}")
  public ResponseEntity<FoodDTO> getFood(@PathVariable("foodId") Long foodId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(foodService.getFood(foodId));
  }

  @PutMapping("/{foodId}")
  public ResponseEntity<FoodDTO> updateFood(@PathVariable("foodId") Long foodId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(foodService.getFood(foodId));
  }
}
