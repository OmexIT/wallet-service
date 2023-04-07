package com.logispin.wallet.models;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<U> {
  @Column(name = "created_by")
  @CreatedBy
  private U createdBy;

  @Column(name = "created_on")
  @CreatedDate
  private LocalDateTime createdOn;

  @Column(name = "last_modified_by")
  @LastModifiedBy
  private U lastModifiedBy;

  @Column(name = "last_modified_on")
  @LastModifiedDate
  private LocalDateTime lastModifiedOn;
}
