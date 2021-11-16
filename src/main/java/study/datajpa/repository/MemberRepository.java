package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Spring Data JPA : @Repository 생략 가능
// T 엔티티 타입, ID 엔티티의 식별자타입
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //find ~ By 사이의 문자는 쿼리에 영향을 주지 않아 임의로 사용하면 된다.
    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUserName")
        // Member Entity의 @namedQuery를 찾는다. - 생략해도 메소드명으로 알아서 찾는다.
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 실무에서는 보통 findByUsernameAndAgeGreaterThan처럼 메소드명으로 쿼리를 만들거나(쿼리가 길어지면 메소드명도 길어지는 치명적인 단점)
     * 위처럼 Entity의 @namedQuery를 사용하지 않고
     * 아래처럼 바로 Repository에 쿼리를 정의하는 방식의 @Query를 사용한다.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO를 조회할 때는 new operation을 사용해야한다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // in절을 사용해야할 때는 컬렉션을 이용한다.
    @Query("select m from Member m where m.username in :names")
    // List<Member> findByNames(@Param("names") List<String> names);
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 다양한 반환타입을 지원한다.
    List<Member> findListByUsername(String username); // 컬렉션

    Member findMemberByUsername(String username); // 단건

    Optional<Member> findOptionalByUsername(String username);

    /**
     * 페이징
     * Pageable은 인터페이스이므로 구현체로 PageRequest를 사용한다.
     * Page : 추가 count 쿼리 결과를 포함하는 페이징
     * Page : 추가 count 쿼리없이 다음 페이지만 확인 가능
     * List : 추가 count 쿼리없이 결과만 반환
     */
    Page<Member> findByUsername(String name, Pageable pageable);
    // Slice<Member> findByUsername(String name, Pageable pageable);
    // List<Member> findByUsername(String name, Pageable pageable);
    // List<Member> findByUsername(String name, Sort sort);

    // 카운트 쿼리를 분리하여 성능을 최적화 할 수 있다.(카운트 쿼리는 굳이 조인을 걸 필요가 없으므로)
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * @Modifying을 넣어야 executeQuery()를 실행한다.
     * 넣지않으면 에러가 난다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // Member를 조회할 때 연관된 팀을 한번에 끌고 온다. => N+1문제 해결
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * 위처럼 @Query와 fetch join을 통하여 team까지 조회하는 방법이 있고 또는
     * findAll()은 기본적으로 member만 조회할 것이므로 아래처럼 오버라이드한 후
     *
     * @EntityGraph()를 사용하여 team까지 한 번에 조회할 수 있다.
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * findMemberEntityGraph(), findEntityByUsername()처럼 사용할 수도 있다.
     */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityByUsername(@Param("username") String username);

    //member enttiy에 @namedEntityGraph을 사용하는 방법은 생략

    /**
     * QueryHint readOnly : 읽기 전용으로써 변경감지를 해도 업데이트되지 않는다.
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String name);

    /**
     * Lock : 깊은 내용이므로 일단 Skip한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);
}