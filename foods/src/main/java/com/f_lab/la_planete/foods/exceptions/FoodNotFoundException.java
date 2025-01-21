package com.f_lab.la_planete.foods.exceptions;

import com.f_lab.la_planete.core.kafka.exceptions.NonRetryableException;

public class FoodNotFoundException extends NonRetryableException {

  public FoodNotFoundException() {
  }

  public FoodNotFoundException(String message) {
    super(message);
  }

  public FoodNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public FoodNotFoundException(Throwable cause) {
    super(cause);
  }

  public FoodNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
