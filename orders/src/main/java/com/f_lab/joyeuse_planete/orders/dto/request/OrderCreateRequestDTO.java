package com.f_lab.joyeuse_planete.orders.dto.request;


import com.f_lab.joyeuse_planete.core.domain.Order;
import com.f_lab.joyeuse_planete.core.domain.OrderStatus;
import com.f_lab.joyeuse_planete.core.events.OrderCreatedEvent;
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
public class OrderCreateRequestDTO {

  @JsonProperty("food_id")
  private Long foodId;

  @JsonProperty("food_name")
  private String foodName;

  @JsonProperty("store_id")
  private Long storeId;

  @JsonProperty("total_cost")
  private BigDecimal totalCost;

  @JsonProperty("quantity")
  private int quantity;

  @JsonProperty("voucher_id")
  private Long voucherId;

  @JsonProperty("payment_information")
  private PaymentInformation paymentInformation;


  public Order toEntity() {
    return Order.builder()
        .status(OrderStatus.READY)
        .totalCost(totalCost)
        .quantity(quantity)
        .build();
  }

  public OrderCreatedEvent toEvent(Long orderId) {
    return OrderCreatedEvent.builder()
        .orderId(orderId)
        .foodId(foodId)
        .foodName(foodName)
        .storeId(storeId)
        .quantity(quantity)
        .totalAmount(totalCost)
        .voucherId(voucherId)
        .paymentInformation(paymentInformation.toPaymentInformation())
        .build();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PaymentInformation {

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("card_holdername")
    private String cardHolderName;

    @JsonProperty("expiry_date")
    private String expiryDate;

    @JsonProperty("cvc")
    private String cvc;

    public OrderCreatedEvent.PaymentInformation toPaymentInformation() {
      return OrderCreatedEvent.PaymentInformation.builder()
          .cardNumber(cardNumber)
          .cardHolderName(cardHolderName)
          .expiryDate(expiryDate)
          .cvc(cvc)
          .build();
    }
  }
}
