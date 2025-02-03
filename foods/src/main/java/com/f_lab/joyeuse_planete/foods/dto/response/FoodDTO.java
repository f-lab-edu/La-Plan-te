package com.f_lab.joyeuse_planete.foods.dto.response;

import com.f_lab.joyeuse_planete.core.domain.Food;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDTO {

  @JsonProperty("food_id")
  private Long foodId;

  @JsonProperty("store_id")
  private Long storeId;

  @JsonProperty("currency_id")
  private Long currencyId;

  @JsonProperty("food_name")
  private String foodName;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("total_quantity")
  private int totalQuantity;

  @JsonProperty("currency_code")
  private String currencyCode;

  @JsonProperty("currency_symbol")
  private String currencySymbol;

  public static FoodDTO from(Food food) {
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
