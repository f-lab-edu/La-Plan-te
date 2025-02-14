package com.f_lab.joyeuse_planete.core.aspect;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;
import com.f_lab.joyeuse_planete.core.util.time.TimeConstantsString;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LockRetryAspect {
  private static final int FIRST_WAIT_INTERVAL = Integer.parseInt(TimeConstantsString.ONE_SECOND);
  private static final int MULTIPLIER = 2;

  @Around("@annotation(retry)")
  public Object lockRetry(ProceedingJoinPoint joinPoint, RetryOnLockFailure retry) {
    int stopInterval = FIRST_WAIT_INTERVAL;

    for (int attempts = 0; attempts < retry.value(); attempts++) {
      try {
        return joinPoint.proceed();
      } catch (PessimisticLockException | LockTimeoutException e) {
        LogUtil.retry(attempts, retry.value(), joinPoint.getSignature().toString());

        // 재시도 전 잠시 멈추고 다시 시작
        // 각 시도 마다 WAIT_INTERVAL 이 MULTIPLIER 에 상응하는 값을 지수적으로 늘어납니다 (backoff)
        // synchronised retry 를 피하기 위해 random 값을 기존 WAIT_INTERVAL 에서 10% ~ 30% 상응하는 값을 더합니다.
        try {
          double PERCENTAGE = 0.1 + (Math.random() * 0.2);
          int RANDOM_FACTOR = (int) (stopInterval * PERCENTAGE);
          stopInterval = stopInterval * (int) Math.pow(MULTIPLIER, attempts) - RANDOM_FACTOR;
          Thread.sleep(stopInterval);

        } catch (InterruptedException ex) {
          throw new JoyeusePlaneteApplicationException(ErrorCode.LOCK_ACQUISITION_FAIL_EXCEPTION, e);
        }

      } catch (Throwable e) {
        LogUtil.exception(joinPoint.getSignature().toString(), e);
        throw new JoyeusePlaneteApplicationException(ErrorCode.LOCK_ACQUISITION_FAIL_EXCEPTION, e);
      }
    }

    throw new JoyeusePlaneteApplicationException(ErrorCode.LOCK_ACQUISITION_FAIL_EXCEPTION);
  }
}
