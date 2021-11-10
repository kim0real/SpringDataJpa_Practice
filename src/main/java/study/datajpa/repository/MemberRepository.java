package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

// Spring Data JPA : @Repository 생략 가능
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //find ~ By 사이의 문자는 쿼리에 영향을 주지 않아 임의로 사용하면 된다.
    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUserName") // Member Entity의 @namedQuery를 찾는다. - 생략해도 메소드명으로 알아서 찾는다.
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
    List<Member> findByNames(@Param("names") List<String> names);

    // 다양한 반환타입을 지원한다.
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

}