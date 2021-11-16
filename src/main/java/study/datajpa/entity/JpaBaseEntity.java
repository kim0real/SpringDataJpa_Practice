package study.datajpa.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
/**
 * Auditing : 생성일, 수정일 등을 만들어준다.
 *
 * 스프링부트 어플리케이션에서 @EnableJpaAuditing 어노테이션을 넣고
 * 엔티티에 @EntityListeners(AuditingEntityListener.class)를 통해 더 간단히 해결할 수 있다.
 */
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void perPrsist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now; // 최초에 update일자도 넣는 것이 쿼리 조회나 수정 시 편하다.
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

    }
}
