package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
/**
 * Auditing
 * 시간만 필요하면 BaseTimeEntity를 상속받으면 되고
 * 작성자, 수정자까지 필요하면 BaseEntity를 상속받으면 되므로 이런 식으로 나누면 편리하다.
 */
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @CreatedBy
    private String lastModifiedBy;



}
