package com.f_lab.joyeuse_planete.core.domain.base;


import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public abstract class BaseEntity extends BaseTimeEntity {

  // TODO @CreatedBy & @LastModifiedBy AuditorAware 및 Spring Security 활용하여 구현해놓기


  private boolean isDeleted;
}
